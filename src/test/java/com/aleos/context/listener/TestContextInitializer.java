package com.aleos.context.listener;

import com.aleos.context.ApplicationContextConfiguration;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.slf4j.LoggerFactory.getLogger;

public class TestContextInitializer implements TestExecutionListener {

    private static final Logger logger = getLogger(TestContextInitializer.class);

    private static ServiceLocator serviceLocator;

    @Container
    private static PostgreSQLContainer<?> postgresContainer;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        postgresContainer = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpassword");

        postgresContainer.start();

        logger.info("Postgres container started with database: {}, username: {}", postgresContainer.getDatabaseName(), postgresContainer.getUsername());

        System.setProperty("DB_URL", postgresContainer.getJdbcUrl());
        System.setProperty("DB_USER", postgresContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgresContainer.getPassword());


        serviceLocator = new BeanFactory(ApplicationContextConfiguration.class);

        logger.info("Test ApplicationContext initialized successfully.");
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        closeEntityManagerFactory();
        shutdownPostgresContainer();
    }

    public static <T> T getBean(Class<T> contextKey) {
        return serviceLocator.getBean(contextKey);
    }

    public static ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    private static void closeEntityManagerFactory() {
        serviceLocator.getBean(EntityManagerFactory.class).close();
        logger.info("EntityManagerFactory is closed.");
    }

    private static void shutdownPostgresContainer() {
        postgresContainer.stop();
        logger.info("Postgres container stopped.");
    }
}