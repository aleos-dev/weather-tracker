package com.aleos.security.authorization;

import com.aleos.exception.context.AuthenticationException;
import com.aleos.exception.security.AccessDeniedException;
import com.aleos.http.CustomHttpSession;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.GrantedAuthority;
import com.aleos.security.core.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AuthorizationManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

    private final Map<String, List<Role>> authorizationRules = new LinkedHashMap<>();

    public boolean check(HttpServletRequest req, Supplier<Authentication> authentication) {
        logger.info("Checking authorization for request: {}", req.getRequestURI());

        var auth = getAuthenticatedUser(authentication);
        var requestURI = req.getRequestURI();
        List<Role> allowedRoles = getAllowedRolesForRequest(requestURI);

        boolean isAllowed = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);

        if (isRegistrationRequired(isAllowed, auth)) {
            logger.warn("Authentication required for user: {}", auth.getPrincipal());
            setOriginalRequestInSession(req);
            throw new AuthenticationException("Authentication required");
        }
        logger.info("Authorization result for request {}: {}", requestURI, isAllowed);

        return isAllowed;
    }

    public void addRules(Map<String, List<Role>> rules) {
        logger.debug("Adding authorization rules: {}", rules);
        authorizationRules.putAll(rules);
    }

    private Authentication getAuthenticatedUser(Supplier<Authentication> authentication) {
        var auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            logger.error("Authorization failed. User is not authenticated.");
            throw new AuthenticationException("Not authenticated");
        }

        logger.info("User {} is authenticated.", auth.getPrincipal());
        return auth;
    }

    private List<Role> getAllowedRolesForRequest(String requestUri) {
        logger.debug("Finding allowed roles for URI: {}", requestUri);
        return authorizationRules.entrySet().stream()
                .filter(entry -> requestUri.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> {
                    logger.warn("Access denied for URI: {}", requestUri);
                    return new AccessDeniedException("Access denied");
                });
    }

    private void setOriginalRequestInSession(HttpServletRequest req) {
        var session = (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
        if (session.getOriginalRequest() == null) {
            logger.debug("Set original request in session: {}", req.getRequestURI());
            session.setOriginalRequest(req.getRequestURI());
        }
    }

    private boolean isRegistrationRequired(boolean isAllowed, Authentication auth) {
        boolean registrationRequired = !isAllowed && Role.ANONYMOUS.name().equals(auth.getPrincipal());

        logger.debug("Registration required: {}", registrationRequired);
        return registrationRequired;
    }
}
