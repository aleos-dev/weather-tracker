package com.aleos.context.listener;

import com.aleos.context.ApplicationContextConfiguration;
import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.slf4j.Logger;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.slf4j.LoggerFactory.getLogger;

public class TestContextInitializer implements TestExecutionListener {

    private static final Logger logger = getLogger(TestContextInitializer.class);

    @Getter
    private static ServiceLocator serviceLocator;

    @Container
    private static PostgreSQLContainer<?> postgresContainer;

    static {
        try {
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
        } catch (Exception e) {
            logger.error("Test ApplicationContext failed to start", e);
            System.exit(1);
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        closeEntityManagerFactory();
        shutdownPostgresContainer();
    }

    public static <T> T getBean(Class<T> clazz) {
        return serviceLocator.getBean(clazz);
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