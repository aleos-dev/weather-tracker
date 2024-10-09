package com.aleos.context.listener;

import com.aleos.context.ApplicationContextConfiguration;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationContextInitializer implements ServletContextListener {

    public static final Logger logger = LoggerFactory.getLogger(ApplicationContextInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        injectFactoryBean(sce);
    }


    private void injectFactoryBean(ServletContextEvent sce) {
        try {
            var factory = new BeanFactory(ApplicationContextConfiguration.class);
            sce.getServletContext().setAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY, factory);
        } catch (Exception e) {
            logger.error("App failed to start", e);
        }
    }

     @Override
    public void contextDestroyed(ServletContextEvent sce) {
        var locator = (ServiceLocator) sce.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        locator.getBean(EntityManagerFactory.class).close();
    }
}
