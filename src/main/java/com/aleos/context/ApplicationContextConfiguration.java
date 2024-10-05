package com.aleos.context;


import com.aleos.annotation.Bean;
import com.aleos.http.SessionManager;
import com.aleos.repository.UserRepository;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.encoder.BCryptPasswordEncoder;
import com.aleos.security.encoder.PasswordEncoder;
import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.service.AuthenticationService;
import com.aleos.service.BaseAuthenticationService;
import com.aleos.util.Properties;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.flywaydb.core.Flyway;

public class ApplicationContextConfiguration {

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

        return Persistence.createEntityManagerFactory(
                Properties.get("hibernate.persistence.unit.name").orElse("default"));
    }

    @Bean
    public Flyway flyway() {
        return Flyway.configure().dataSource(
                Properties.get("database.url").orElseThrow(),
                Properties.get("database.user").orElseThrow(),
                Properties.get("database.password").orElseThrow()
        ).load();
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
}
