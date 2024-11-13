package com.aleos.context.listener;

import com.aleos.context.Properties;
import com.aleos.exception.context.TemplateEngineInitializationException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

/**
 * Initializes and configures a Thymeleaf Template Engine within a ServletContext.
 * <p>
 * This class implements the ServletContextListener interface and prepares the
 * Thymeleaf Template Engine when the servlet context is initialized. It sets various
 * properties of the template engine such as template mode, prefix, suffix, character encoding,
 * and cache settings. The initialized engine is then stored in the servlet context for
 * later retrieval and use in rendering templates.
 * <p>
 * The class also handles exceptions that occur during the initialization process,
 * logging relevant error messages and throwing a TemplateEngineInitializationException.
 * <p>
 * Key Points:
 * - Implements ServletContextListener to hook into the servlet context lifecycle.
 * - Configures the Template Engine with settings like template mode, prefix, suffix, encoding, and caching.
 * - Stores the initialized Template Engine in the servlet context under a specific key.
 * - Handles and logs exceptions that occur during initialization.
 */
public class TemplateEngineInitializer implements ServletContextListener {

    public static final Logger logger = LoggerFactory.getLogger(TemplateEngineInitializer.class);

    public static final String TEMPLATE_ENGINE_CONTEXT_KEY = "TEMPLATE_ENGINE_CONTEXT_KEY";


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Thymeleaf Template Engine...");

        try {
            JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(sce.getServletContext());
            logger.debug("JakartaServletWebApplication successfully built.");

            final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

            templateResolver.setTemplateMode(TemplateMode.HTML);
            logger.debug("Template mode set to HTML.");

            templateResolver.setPrefix(Properties.get("thymeleaf.url.path.template").orElse("/"));
            templateResolver.setSuffix(".html");
            templateResolver.setCharacterEncoding("UTF-8");
            logger.debug("Template prefix, suffix, and encoding set.");

            templateResolver.setCacheable(false);  // Set to true for production
            templateResolver.setCacheTTLMs(3600000 * 24L);  // Cache for 24 hours
            logger.debug("Template caching set to {} and TTL set to {} ms.", templateResolver.isCacheable(), templateResolver.getCacheTTLMs());

            final TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            sce.getServletContext().setAttribute(TEMPLATE_ENGINE_CONTEXT_KEY, templateEngine);
            logger.info("Thymeleaf Template Engine initialized and stored in ServletContext with key '{}'.", TEMPLATE_ENGINE_CONTEXT_KEY);

        } catch (Exception e) {
            logger.error("Error during Thymeleaf Template Engine initialization: ", e);
            throw new TemplateEngineInitializationException("Failed to initialize Thymeleaf Template Engine.");
        }
    }
}
