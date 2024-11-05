package com.aleos.context;


import com.aleos.context.annotation.Bean;
import com.aleos.http.SessionManager;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.repository.UserRepository;
import com.aleos.repository.VerificationTokenDao;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.encoder.BCryptPasswordEncoder;
import com.aleos.security.encoder.PasswordEncoder;
import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.flywaydb.core.Flyway;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.http.HttpClient;
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
                        Properties.get(DB_URL_ENV).orElseThrow(),
                        Properties.get(DB_USER_ENV).orElseThrow(),
                        Properties.get(DB_PASSWORD_ENV).orElseThrow()
                )
                .cleanDisabled(Boolean.parseBoolean(Properties.get("flyway.cleanDisabled").orElse("true")))
                .load();
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        return new JedisPool(poolConfig, "localhost", 6379);
    }

    @Bean
    public SessionManager sessionManager(JedisPool jedisPool, ObjectMapper objectMapper) {
        return new SessionManager(jedisPool, objectMapper);
    }

    // Services

    @Bean
    public UserService userService(UserRepository userRepository,
                                   WeatherApiClient weatherApiClient,
                                   PasswordEncoder passwordEncoder,
                                   ModelMapper modelMapper) {
        return new UserService(userRepository, weatherApiClient, passwordEncoder, modelMapper);
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

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public WeatherApiClient weatherApiClient(HttpClient httpClient, ObjectMapper objectMapper) {
        return new OpenWeatherApiClient(httpClient, objectMapper);
    }

    // Repositories

    @Bean
    public VerificationTokenDao verificationTokenDao(EntityManagerFactory entityManagerFactory) {
        return new VerificationTokenDao(entityManagerFactory, UserVerificationToken.class);
    }

    @Bean
    public UserRepository userRepository(EntityManagerFactory entityManagerFactory, VerificationTokenDao verificationTokenDao) {
        return new UserRepository(entityManagerFactory, verificationTokenDao);
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

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());  // Register JDK8 module to handle Optional

        return objectMapper;
    }

    private Map<String, String> loadHibernateProperties() {
        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("hibernate.connection.url", Properties.get(DB_URL_ENV).orElseThrow());
        propertiesMap.put("hibernate.connection.username", Properties.get(DB_USER_ENV).orElseThrow());
        propertiesMap.put("hibernate.connection.password", Properties.get(DB_PASSWORD_ENV).orElseThrow());
        return propertiesMap;
    }
}
