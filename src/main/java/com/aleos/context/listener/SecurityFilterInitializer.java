package com.aleos.context.listener;

import com.aleos.security.configuration.SecurityInitializer;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * SecurityFilterInitializer is a listener that initializes security filters
 * during the web application's startup phase. It implements the
 * ServletContextListener interface and overrides the contextInitialized method
 * to set up the security context using a given ServiceLocator.
 */
public class SecurityFilterInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var locator = (ServiceLocator) sce.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        SecurityInitializer.initSecurityContext(locator);
    }
}
