package com.aleos.context.listener;

import com.aleos.security.configuration.SecurityInitializer;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class SecurityFilterInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        var locator = (ServiceLocator) sce.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        SecurityInitializer.initSecurityContext(locator);
    }
}
