package com.aleos.security.configuration;

import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.core.Role;
import com.aleos.security.web.DefaultSecurityFilterChain;
import com.aleos.security.web.SecurityFilterChain;
import com.aleos.security.web.context.SecurityContextRepository;
import com.aleos.security.web.filters.*;
import com.aleos.service.AuthenticationService;
import com.aleos.context.servicelocator.ServiceLocator;
import jakarta.servlet.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SecurityInitializer {

    private SecurityInitializer() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void initSecurityContext(ServiceLocator locator) {
        var configurer = new SecurityFilterChainConfigurer();

        applyAuthorizationRules(locator);

        DefaultSecurityFilterChain defaultSecurityFilterChain = configurer
                .setPattern("/")
                .addFilter(createSecurityContextHolderFilter(locator))
                .addFilter(createAuthenticationFilterImpl(locator))
                .addFilter(createAnonymousAuthenticationFilter())
                .addFilter(createAuthorizationFilter(locator))
                .build();

        locator.registerBean(SecurityFilterChain.class, defaultSecurityFilterChain);
    }

    private static void applyAuthorizationRules(ServiceLocator locator) {
        Map<String, List<Role>> authorizationRules = new HashMap<>();
        authorizationRules.put("/api/v1/admin", List.of(Role.ADMIN));
        authorizationRules.put("/api/v1/", List.of(Role.USER, Role.ADMIN));
        authorizationRules.put("/", List.of(Role.ANONYMOUS, Role.USER, Role.ADMIN));

        var authorizationManager = locator.getBean(AuthorizationManager.class);
        authorizationManager.addRules(authorizationRules);
    }

    private static Filter createAuthorizationFilter(ServiceLocator locator) {
        return new AuthorizationFilter(locator.getBean(AuthorizationManager.class));
    }

    private static Filter createAuthenticationFilterImpl(ServiceLocator locator) {
        return new AuthenticationFilter(locator.getBean(AuthenticationService.class));
    }

    private static SecurityContextHolderFilter createSecurityContextHolderFilter(ServiceLocator locator) {
        return new SecurityContextHolderFilter(locator.getBean(SecurityContextRepository.class));
    }

    private static Filter createAnonymousAuthenticationFilter() {
        return new AnonymousAuthenticationFilter();
    }
}
