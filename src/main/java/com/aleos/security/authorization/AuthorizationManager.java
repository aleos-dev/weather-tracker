package com.aleos.security.authorization;

import com.aleos.exception.context.AuthenticationException;
import com.aleos.exception.security.ResourceNotFoundException;
import com.aleos.http.CustomHttpSession;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.GrantedAuthority;
import com.aleos.security.core.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The AuthorizationManager class is responsible for managing the authorization of HTTP requests
 * based on predefined authorization rules and the authenticated user's roles.
 * <p>
 * This class logs all the major steps of the authorization process and throws exceptions
 * in cases where authorization or authentication fails.
 */
@Slf4j
public class AuthorizationManager {

    private final Map<String, List<Role>> authorizationRules = new LinkedHashMap<>();

    /**
     * Checks the authorization of the current request based on the given authentication mechanism.
     *
     * @param req the HTTP request to be checked
     * @param authentication a supplier that provides the current authentication details
     * @return true if the request is authorized, false otherwise
     * @throws AuthenticationException if authentication is required but not present or valid
     */
    public boolean check(HttpServletRequest req, Supplier<Authentication> authentication) {
        var requestURI = req.getRequestURI();
        log.info("Checking authorization for request: {}", requestURI);

        var auth = getAuthenticatedUser(authentication);
        List<Role> allowedRoles = getAllowedRolesForRequest(requestURI);

        boolean isAllowed = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);

        if (isRegistrationRequired(isAllowed, auth)) {
            log.warn("Authentication required for user: {}", auth.getPrincipal());
            setOriginalRequestInSession(req);
            throw new AuthenticationException("Authentication required");
        }
        log.info("Authorization result for request {}: {}", requestURI, isAllowed);

        return isAllowed;
    }

    /**
     * Adds a set of authorization rules to the existing set.
     *
     * @param rules a map where the key is a string representing the rule name
     *              and the value is a list of Role objects associated with that rule
     */
    public void addRules(Map<String, List<Role>> rules) {
        log.debug("Adding authorization rules: {}", rules);
        authorizationRules.putAll(rules);
    }

    private Authentication getAuthenticatedUser(Supplier<Authentication> authentication) {
        var auth = authentication.get();

        if ((auth != null && (auth.isAuthenticated() || auth.isAnonymous()))) {
            log.warn("Authentication is retrieved for {}", auth.getPrincipal());
            return auth;
        }

        log.error("Authorization failed. User is not authenticated.");
        throw new AuthenticationException("Not authenticated");
    }

    private List<Role> getAllowedRolesForRequest(String requestUri) {
        log.debug("Retrieving authorization roles for the requested URI: {}", requestUri);
        return authorizationRules.entrySet().stream()
                .filter(entry -> requestUri.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Resource: {} not found", requestUri);
                    return new ResourceNotFoundException("Resource not found: " + requestUri);
                });
    }

    private void setOriginalRequestInSession(HttpServletRequest req) {
        var session = (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
        if (session.getOriginalRequest() == null) {
            log.debug("Set original request in session: {}", req.getRequestURI());
            session.setOriginalRequest(req.getRequestURI());
        }
    }

    private boolean isRegistrationRequired(boolean isAllowed, Authentication auth) {
        boolean registrationRequired = !isAllowed && auth.isAnonymous();

        log.debug("Registration required: {}", registrationRequired);
        return registrationRequired;
    }
}
