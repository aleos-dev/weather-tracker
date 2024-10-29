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

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

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
        logger.info("Servlet context is being destroyed. Cleaning up resources...");

        closeEntityManagerFactory(sce);
        unregisterJdbcDrivers();

        logger.info("Context destruction complete.");
    }

    private void closeEntityManagerFactory(ServletContextEvent sce) {
        logger.info("EntityManagerFactory is closed.");
        var locator = (ServiceLocator) sce.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        locator.getBean(EntityManagerFactory.class).close();
    }

    private void unregisterJdbcDrivers() {

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info("Successfully deregistered JDBC driver: {}", driver);
            } catch (SQLException e) {
                logger.error("Error deregistering JDBC driver: {}", driver, e);
            }
        }

    }
}
