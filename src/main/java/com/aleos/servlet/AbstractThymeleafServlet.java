package com.aleos.servlet;

import com.aleos.context.listener.TemplateEngineInitializer;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import com.aleos.exception.ParseDtoException;
import com.aleos.exception.context.BeanInitializationException;
import com.aleos.exception.servlet.RedirectException;
import com.aleos.exception.servlet.ResponseWritingException;
import com.aleos.model.annotation.RequestParam;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AbstractThymeleafServlet extends HttpServlet {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractThymeleafServlet.class);

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

    protected void sendRedirect(String redirectUrl, HttpServletResponse res) {
        try {
            res.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new RedirectException("Failed to process redirect to %s".formatted(redirectUrl), e);
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

    protected <T> Optional<List<String>> validatePayload(Validator payloadValidator, T inputPayload) {
        Set<ConstraintViolation<T>> constraintViolations = payloadValidator.validate(inputPayload);

        if (constraintViolations.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                constraintViolations.stream()
                        .map(this::formatConstraintViolation)
                        .toList()
        );
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
