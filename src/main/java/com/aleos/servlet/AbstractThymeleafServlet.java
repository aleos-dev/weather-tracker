package com.aleos.servlet;

import com.aleos.context.Properties;
import com.aleos.context.listener.TemplateEngineInitializer;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import com.aleos.exception.context.BeanInitializationException;
import com.aleos.exception.service.ParseDtoException;
import com.aleos.exception.servlet.RedirectException;
import com.aleos.exception.servlet.ResponseWritingException;
import com.aleos.http.CustomHttpSession;
import com.aleos.model.ErrorData;
import com.aleos.model.annotation.RequestParam;
import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContext;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class AbstractThymeleafServlet extends HttpServlet {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractThymeleafServlet.class);

    protected static final String DEFAULT_WELCOME_URI = Properties.get("base.url").orElse("/api/v1/welcome");
    protected static final String DEFAULT_WEATHER_URI = Properties.get("base.auth.url").orElse("/api/v1/weather");
    protected static final String DEFAULT_AUTH_URI = Properties.get("auth.url").orElse("/api/v1/sign-in");

    protected static final String ERROR_ATTRIBUTE_KEY = "errors";
    protected static final String MESSAGE_ATTRIBUTE_KEY = "message";

    protected transient ITemplateEngine templateEngine;
    protected transient ServiceLocator serviceLocator;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        templateEngine = retrieveTemplateEngine(config);
        serviceLocator = retrieveServiceLocator(config);
    }

    protected void processTemplate(String template, HttpServletRequest req, HttpServletResponse res) {
        try {
            var ctx = buildWebContext(req, res);
            templateEngine.process(template, ctx, res.getWriter());
        } catch (IOException e) {
            throw new ResponseWritingException("Failed to write response", e);
        }
    }

    protected <T> T parseSimpleDto(Class<T> dtoClass, HttpServletRequest req) {
        Field[] fields = dtoClass.getDeclaredFields();
        Constructor<?> dtoConstructor = getDeclaredConstructor(dtoClass, fields);

        Object[] args = Arrays.stream(fields)
                .map(field -> {
                    field.setAccessible(true);
                    if (field.getType() != String.class) {
                        throw new ParseDtoException("Invalid DTO: Only String constructors are allowed for parsing.");
                    }
                    RequestParam annotation = field.getAnnotation(RequestParam.class);
                    String paramName = annotation != null ? annotation.value() : field.getName();
                    return req.getParameter(paramName);
                })
                .toArray();

        return (T) createObject(dtoConstructor, args);
    }

    protected <T> Optional<ErrorData> validatePayload(Validator payloadValidator, T inputPayload) {
        Set<ConstraintViolation<T>> constraintViolations = payloadValidator.validate(inputPayload);

        if (constraintViolations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                new ErrorData(
                        constraintViolations.stream()
                                .map(this::formatConstraintViolation)
                                .toList()
                ));
    }

    protected void sendRedirect(String redirectUrl, HttpServletResponse res) {
        try {
            res.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RedirectException("Failed to process redirect to %s".formatted(redirectUrl), e);
        }
    }

    protected void renderErrorPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
        req.setAttribute(ERROR_ATTRIBUTE_KEY, ErrorData.fromSingleError(errorMessage));
        processTemplate("errorPage", req, res);
    }

    protected CustomHttpSession getSessionContext(HttpServletRequest req) {
        return (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
    }

    protected String retrieveAuthenticationPrincipal(HttpServletRequest req) {
        var securityContext = (SecurityContext) getSessionContext(req).
                getAttribute(HttpSessionSecurityContextRepository.SECURITY_CONTEXT_KEY);

        return securityContext.getAuthentication().getPrincipal();
    }

    protected boolean hasNoErrorAttribute(HttpServletRequest req) {
        return req.getAttribute(ERROR_ATTRIBUTE_KEY) == null;
    }

    private Object createObject(Constructor<?> dtoConstructor, Object[] args) {
        try {
            return dtoConstructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Creation of the dto instance is failed");
            throw new ParseDtoException("Creation of the dto instance is failed", e);
        }
    }

    private Constructor<?> getDeclaredConstructor(Class<?> dtoClass, Field[] fields) {
        try {
            return dtoClass.getDeclaredConstructor(
                    Arrays.stream(fields)
                            .map(Field::getType)
                            .toArray(Class<?>[]::new)
            );
        } catch (NoSuchMethodException e) {
            String errorMessageTemplate = "The parsing for the dto: %s was failed";
            String errorMessage = errorMessageTemplate.formatted(dtoClass.getSimpleName());
            logger.error(errorMessage);
            throw new ParseDtoException(errorMessage, e);
        }
    }

    private WebContext buildWebContext(HttpServletRequest req, HttpServletResponse res) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());

        return new WebContext(application.buildExchange(req, res));
    }

    private ITemplateEngine retrieveTemplateEngine(ServletConfig config) {
        var obj = config.getServletContext()
                .getAttribute(TemplateEngineInitializer.TEMPLATE_ENGINE_CONTEXT_KEY);

        if (obj instanceof ITemplateEngine templateEngineObj) {
            return templateEngineObj;
        } else {
            throw new BeanInitializationException("TemplateEngine bean is not of the correct type");
        }
    }

    private ServiceLocator retrieveServiceLocator(ServletConfig config) {
        var obj = config.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);

        if (obj instanceof ServiceLocator serviceLocatorObj) {
            return serviceLocatorObj;
        } else {
            throw new BeanInitializationException("ServiceLocator bean is not of the correct type");
        }
    }

    private <T> String formatConstraintViolation(ConstraintViolation<T> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
