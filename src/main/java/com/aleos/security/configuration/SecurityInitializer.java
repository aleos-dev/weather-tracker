package com.aleos.security.configuration;

import com.aleos.context.Properties;
import com.aleos.context.servicelocator.ServiceLocator;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.core.Role;
import com.aleos.security.web.DefaultSecurityFilterChain;
import com.aleos.security.web.SecurityFilterChain;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.security.web.filters.*;
import com.aleos.service.AuthenticationService;
import jakarta.servlet.Filter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to initialize security context and configure the security filter chain.
 * <p>
 * This class sets up various security filters for the application and defines authorization rules
 * for different URL patterns.
 */
public final class SecurityInitializer {

    private SecurityInitializer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Initializes the security context by configuring and registering the security filter chain.
     *
     * This method sets up various security filters including authentication, authorization,
     * and exception handling filters. It also applies the necessary authorization rules
     * to the service locator and ensures the character encoding filter is conditionally applied.
     *
     * @param locator the ServiceLocator instance used to retrieve and register security-related beans.
     */
    public static void initSecurityContext(ServiceLocator locator) {
        var configurer = new SecurityFilterChainConfigurer();

        applyAuthorizationRules(locator);

        boolean encodingEnabled = Properties.get("security.http.utf8.encoding.enabled")
                .map(Boolean::parseBoolean)
                .orElse(false);

        DefaultSecurityFilterChain defaultSecurityFilterChain = configurer
                .setPattern("/")
                .addFilter(createCharacterEncodingFilter(), encodingEnabled)
                .addFilter(createSecurityContextHolderFilter(locator))
                .addFilter(createAuthenticationFilterImpl(locator))
                .addFilter(createAnonymousAuthenticationFilter())
                .addFilter(createExceptionTranslationFilter())
                .addFilter(createAuthorizationFilter(locator))
                .build();

        locator.registerBean(SecurityFilterChain.class, defaultSecurityFilterChain);
    }

    /**
     * Applies authorization rules to the provided ServiceLocator instance.
     *
     * This method sets up a mapping of URL paths to the roles that are authorized to access them.
     * It then retrieves the AuthorizationManager from the provided ServiceLocator and adds the
     * authorization rules to it.
     *
     * @param locator the ServiceLocator instance used to retrieve the AuthorizationManager
     */
    private static void applyAuthorizationRules(ServiceLocator locator) {
        String baseUrl = Properties.get("base.url").orElse("/");

        Map<String, List<Role>> authorizationRules = new LinkedHashMap<>();
        authorizationRules.put(baseUrl, List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/css", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/resources", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/favicon.ico", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));

        authorizationRules.put("/api/v1/locations", List.of(Role.USER, Role.ADMIN));

        authorizationRules.put("/api/v1/sign-in", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/api/v1/sign-out", List.of(Role.USER, Role.ADMIN));
        authorizationRules.put("/api/v1/sign-up", List.of(Role.ANONYMOUS));
        authorizationRules.put("/api/v1/error", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/api/v1/verify", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));
        authorizationRules.put("/api/v1/weather", List.of(Role.USER, Role.ADMIN));
        authorizationRules.put("/api/v1/", List.of(Role.ADMIN));

        var authorizationManager = locator.getBean(AuthorizationManager.class);
        authorizationManager.addRules(authorizationRules);
    }

    private static Filter createCharacterEncodingFilter() {
        return new CharacterEncodingFilter();
    }

    private static Filter createSecurityContextHolderFilter(ServiceLocator locator) {
        return new SecurityContextHolderFilter(locator.getBean(SecurityContextRepository.class));
    }

    private static Filter createAuthenticationFilterImpl(ServiceLocator locator) {
        return new AuthenticationFilter(locator.getBean(AuthenticationService.class));
    }

    private static Filter createAnonymousAuthenticationFilter() {
        return new AnonymousAuthenticationFilter();
    }

    private static Filter createExceptionTranslationFilter() {
        return new ExceptionTranslationFilter();
    }

    private static Filter createAuthorizationFilter(ServiceLocator locator) {
        return new AuthorizationFilter(locator.getBean(AuthorizationManager.class));
    }
}
