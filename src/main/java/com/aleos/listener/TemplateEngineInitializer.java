package com.aleos.listener;

import com.aleos.util.Properties;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class TemplateEngineInitializer implements ServletContextListener {

    public static final String TEMPLATE_ENGINE_CONTEXT_KEY = "TEMPLATE_ENGINE_CONTEXT_KEY";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
          JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(sce.getServletContext());

        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateResolver.setPrefix(Properties.get("thymeleaf.url.path.template").orElse("/"));
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");

        // todo: set to true on production
        templateResolver.setCacheable(false);
        templateResolver.setCacheTTLMs(3600000 * 24L);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        sce.getServletContext().setAttribute(TEMPLATE_ENGINE_CONTEXT_KEY, templateEngine);
    }
}
