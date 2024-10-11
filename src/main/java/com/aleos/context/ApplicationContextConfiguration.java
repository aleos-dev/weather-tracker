package com.aleos.context;


import com.aleos.context.annotation.Bean;
import com.aleos.http.SessionManager;
import com.aleos.model.entity.User;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.repository.UserDao;
import com.aleos.repository.UserRepository;
import com.aleos.repository.VerificationTokenDao;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.encoder.BCryptPasswordEncoder;
import com.aleos.security.encoder.PasswordEncoder;
import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.service.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.flywaydb.core.Flyway;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

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

    // Services

    @Bean
    public UserService userService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   ModelMapper modelMapper) {
        return new UserService(userRepository, passwordEncoder, modelMapper);
    }

    @Bean
    public AuthenticationService authenticationService(UserService userService) {
        return userService;
    }

    @Bean
    public RegistrationService registrationService(UserService userService) {
        return userService;
    }

    @Bean
    public VerificationService verificationService(UserService userService) {
        return userService;
    }

    @Bean
    public EmailService emailService() {
        return new EmailService();
    }

    // Repositories

    @Bean
    public UserDao userDao(EntityManagerFactory entityManagerFactory) {
        return new UserDao(entityManagerFactory, User.class);
    }

    @Bean
    public VerificationTokenDao verificationTokenDao(EntityManagerFactory entityManagerFactory) {
        return new VerificationTokenDao(entityManagerFactory, UserVerificationToken.class);
    }

    @Bean
    public UserRepository userRepository(UserDao userDao, VerificationTokenDao verificationTokenDao) {
        return new UserRepository(userDao, verificationTokenDao);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        return mapper;
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
