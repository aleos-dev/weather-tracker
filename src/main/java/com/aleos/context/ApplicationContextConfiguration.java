package com.aleos.context;


import com.aleos.context.annotation.Bean;
import com.aleos.http.SessionManager;
import com.aleos.repository.UserRepository;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.encoder.BCryptPasswordEncoder;
import com.aleos.security.encoder.PasswordEncoder;
import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.service.AuthenticationService;
import com.aleos.service.BaseAuthenticationService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.flywaydb.core.Flyway;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContextConfiguration {

    private static final String DB_URL_ENV = "DB_URL";
    private static final String DB_USER_ENV = "DB_USER";
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    @Bean
    public ValidatorFactory validatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    @Bean
    public Validator validator(ValidatorFactory validatorFactory) {
        return validatorFactory.getValidator();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(Flyway flyway) {
        flyway.migrate();
        String persistenceUnitName = Properties.get("hibernate.persistence.unit.name").orElse("default");
        return Persistence.createEntityManagerFactory(persistenceUnitName, loadHibernateProperties());
    }

    @Bean
    public Flyway flyway() {
        return Flyway.configure().dataSource(
                System.getenv(DB_URL_ENV),
                System.getenv(DB_USER_ENV),
                System.getenv(DB_PASSWORD_ENV)).load();
    }

    @Bean
    public SessionManager customHttpSession() {
        return new SessionManager();
    }

    @Bean
    public AuthenticationService authenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new BaseAuthenticationService(userRepository, passwordEncoder);
    }

    @Bean
    public UserRepository userRepository(EntityManagerFactory entityManagerFactory) {
        return new UserRepository(entityManagerFactory);
    }

    // Security beans
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthorizationManager authorizationManager() {
        return new AuthorizationManager();
    }

    private Map<String, String> loadHibernateProperties() {
        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("hibernate.connection.url", System.getenv(DB_URL_ENV));
        propertiesMap.put("hibernate.connection.username", System.getenv(DB_USER_ENV));
        propertiesMap.put("hibernate.connection.password", System.getenv(DB_PASSWORD_ENV));
        return propertiesMap;
    }
}
