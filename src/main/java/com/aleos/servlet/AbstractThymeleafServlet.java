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
import com.aleos.model.ErrorDetails;
import com.aleos.model.annotation.RequestParam;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
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

/**
 * An abstract servlet for handling HTTP requests and rendering Thymeleaf templates.
 * <p>
 * This servlet provides several utility methods to ease template processing,
 * DTO parsing, payload validation, and error handling in derived servlets.
 * <p>
 * Logging is used extensively for tracing and debugging purposes.
 */
@Slf4j
public class AbstractThymeleafServlet extends HttpServlet {

    private static final String ERROR_PAGE_TEMPLATE = "errorPage";
    public static final String ERROR_ATTRIBUTE_KEY = "errors";
    public static final String MESSAGE_ATTRIBUTE_KEY = "message";

    protected static final String WELCOME_URI = Properties.get("base.url").orElse("/api/v1/welcome");
    protected static final String WEATHER_URI = Properties.get("base.auth.url").orElse("/api/v1/weather");
    protected static final String AUTH_URI = Properties.get("auth.url").orElse("/api/v1/sign-in");

    protected transient ITemplateEngine templateEngine;
    protected transient ServiceLocator serviceLocator;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("Initializing AbstractThymeleafServlet");

        templateEngine = retrieveTemplateEngine();
        serviceLocator = retrieveServiceLocator();
    }

    /**
     * Processes the given template and writes the output to the HTTP response.
     *
     * @param template the name of the template to process
     * @param req the {@code HttpServletRequest} object that contains the request the client made to the servlet
     * @param res the {@code HttpServletResponse} object that contains the response the servlet returns to the client
     * @throws ResponseWritingException if there is an error writing the response
     */
    protected void processTemplate(String template, HttpServletRequest req, HttpServletResponse res) {
        log.debug("Processing template: {}", template);
        try (var printWriter = res.getWriter()) {
            var ctx = buildWebContext(req, res);
            templateEngine.process(template, ctx, printWriter);
            log.info("Template {} processed successfully", template);
        } catch (IOException e) {
            throw new ResponseWritingException("Failed to write response for template: %s".formatted(template), e);
        }
    }

    /**
     * Parses a simple Data Transfer Object (DTO) from the given HTTP servlet request.
     *
     * @param dtoClass the class of the DTO to be parsed
     * @param req the HTTP servlet request containing the data for the DTO
     * @return an instance of the parsed DTO
     */
    protected <T> T parseSimpleDto(Class<T> dtoClass, HttpServletRequest req) {
        log.debug("Parsing DTO for class: {}", dtoClass.getSimpleName());
        Field[] fields = dtoClass.getDeclaredFields();
        Constructor<?> dtoConstructor = getDeclaredConstructor(dtoClass, fields);

        Object[] args = getDtoConstructorArguments(req, fields);
        T dtoInstance = createDtoInstance(dtoConstructor, args);
        log.info("DTO parsed successfully for class: {}", dtoClass.getSimpleName());

        return dtoInstance;
    }

    /**
     * Validates the given input payload using the provided payload validator.
     *
     * @param payloadValidator the validator to use for validating the payload
     * @param inputPayload the payload object to be validated
     * @param <T> the type of the payload
     * @return an Optional containing ErrorDetails if there are validation violations, or an empty Optional if the payload is valid
     */
    protected <T> Optional<ErrorDetails> validatePayload(Validator payloadValidator, T inputPayload) {
        log.debug("Validating payload of type: {}", inputPayload.getClass().getSimpleName());
        Set<ConstraintViolation<T>> constraintViolations = payloadValidator.validate(inputPayload);

        if (constraintViolations.isEmpty()) {
            log.info("Payload validation passed for type: {}", inputPayload.getClass().getSimpleName());
            return Optional.empty();
        }

        log.warn("Payload validation failed with {} violations", constraintViolations.size());
        return Optional.of(
                new ErrorDetails(
                        constraintViolations.stream()
                                .map(this::formatConstraintViolation)
                                .toList()
                ));
    }

    /**
     * Sends an HTTP redirect response to the specified URL.
     *
     * @param redirectUrl the URL to redirect to.
     * @param res the HttpServletResponse object to use for sending the redirect.
     * @throws RedirectException if an IOException occurs during the redirect process.
     */
    protected void sendRedirect(String redirectUrl, HttpServletResponse res) {
        log.info("Sending redirect to URL: {}", redirectUrl);
        try {
            res.sendRedirect(redirectUrl);
            log.debug("Redirect to {} successful", redirectUrl);
        } catch (IOException e) {
            throw new RedirectException("Failed to process redirect to %s".formatted(redirectUrl), e);
        }
    }

    /**
     * Renders an error page with the provided error message.
     *
     * @param req the HttpServletRequest object that contains the request the client made to the servlet
     * @param res the HttpServletResponse object that contains the response the servlet returns to the client
     * @param errorMessage the message to be displayed on the error page
     */
    protected void renderErrorPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
        log.warn("Rendering error page with message: {}", errorMessage);
        req.setAttribute(ERROR_ATTRIBUTE_KEY, ErrorDetails.fromSingleError(errorMessage));
        processTemplate(ERROR_PAGE_TEMPLATE, req, res);
    }

    /**
     * Retrieves the session context for the given HTTP servlet request.
     *
     * @param req the HttpServletRequest object from which the session context is retrieved
     * @return the CustomHttpSession object associated with the given request
     */
    protected CustomHttpSession getSessionContext(HttpServletRequest req) {
        return (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
    }

    /**
     * Retrieves the authentication principal from the HTTP servlet request.
     *
     * @param req the HttpServletRequest object from which to retrieve the authentication principal.
     * @return the authentication principal as a String.
     */
    protected String retrieveAuthenticationPrincipal(HttpServletRequest req) {
        return getSessionContext(req).getPrincipal();
    }

    /**
     * Checks if the given HttpServletRequest does not contain an attribute
     * associated with errors.
     *
     * @param req the HttpServletRequest to be checked for the error attribute.
     * @return true if no error attribute is present, false otherwise.
     */
    protected boolean hasNoErrorAttribute(HttpServletRequest req) {
        return req.getAttribute(ERROR_ATTRIBUTE_KEY) == null;
    }

    /**
     * Builds and returns a WebContext based on the given HttpServletRequest and HttpServletResponse.
     *
     * @param req the HttpServletRequest object that contains the request the client made to the servlet
     * @param res the HttpServletResponse object that contains the response the servlet returns to the client
     * @return a WebContext object built based on the provided request and response
     */
    protected WebContext buildWebContext(HttpServletRequest req, HttpServletResponse res) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(req.getServletContext());

        return new WebContext(application.buildExchange(req, res));
    }

    private Object createObject(Constructor<?> dtoConstructor, Object[] args) {
        try {
            return dtoConstructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            var message = "Creation of the DTO instance failed for constructor: {}, dtoConstructor.getName()";
            throw new ParseDtoException(message, e);
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
            String message = "The parsing for the DTO %s failed due to missing constructor.".formatted(dtoClass.getSimpleName());
            throw new ParseDtoException(message, e);
        }
    }

    private ITemplateEngine retrieveTemplateEngine() {
        log.debug("Retrieving TemplateEngine from ServletConfig");
        var obj = getServletContext().getAttribute(TemplateEngineInitializer.TEMPLATE_ENGINE_CONTEXT_KEY);

        if (obj instanceof ITemplateEngine templateEngineObj) {
            return templateEngineObj;
        } else {
            throw new BeanInitializationException("TemplateEngine bean is not of the correct type in ServletContext");
        }
    }

    private ServiceLocator retrieveServiceLocator() {
        log.debug("Retrieving ServiceLocator from ServletConfig");
        var obj = getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);

        if (obj instanceof ServiceLocator serviceLocatorObj) {
            return serviceLocatorObj;
        } else {
            throw new BeanInitializationException("ServiceLocator bean is not of the correct type");
        }
    }

    private <T> String formatConstraintViolation(ConstraintViolation<T> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }

    private static Object[] getDtoConstructorArguments(HttpServletRequest req, Field[] fields) {
        return Arrays.stream(fields)
                .map(field -> {
                    field.setAccessible(true);
                    if (field.getType() != String.class) {
                        throw new ParseDtoException("Invalid DTO: Only String constructors are allowed for parsing.");
                    }
                    RequestParam annotation = field.getAnnotation(RequestParam.class);
                    String paramName = annotation != null ? annotation.value() : field.getName();
                    var pVal = req.getParameter(paramName);
                    log.debug("Parsed parameter {} with value: {}", paramName, pVal);
                    return pVal != null ? pVal.trim() : null;
                })
                .toArray();
    }

    private <T> T createDtoInstance(Constructor<?> dtoConstructor, Object[] args) {
        return (T) createObject(dtoConstructor, args);
    }
}
