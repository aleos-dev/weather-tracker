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

/**
 * ApplicationContextInitializer is a web listener that initializes the application context
 * and injects required factory beans when the servlet context is initialized. It also handles
 * the cleanup of resources during the context destruction.
 * <p>
 * The class implements the ServletContextListener interface to hook into the lifecycle events
 * of the servlet context.
 * <p>
 * Fields:
 * - logger: Logger instance for logging messages.
 * <p>
 * Methods:
 * - contextInitialized(ServletContextEvent sce): Called when the servlet context is initialized.
 * It sets up the BeanFactory and injects it into the context.
 * - contextDestroyed(ServletContextEvent sce): Called when the servlet context is destroyed.
 * It performs cleanup tasks such as closing the EntityManagerFactory and unregistering JDBC drivers.
 * <p>
 * Private Methods:
 * - injectFactoryBean(ServletContextEvent sce): Initializes the BeanFactory and sets it as an attribute
 * in the servlet context. Logs an error message in case of any exception.
 * - closeEntityManagerFactory(ServletContextEvent sce): Closes the EntityManagerFactory bean located via
 * the service locator stored in the servlet context. Logs the closure of the EntityManagerFactory.
 * - unregisterJdbcDrivers(): Deregisters all JDBC drivers registered with the DriverManager and logs the
 * success or failure of each deregistration.
 */
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
