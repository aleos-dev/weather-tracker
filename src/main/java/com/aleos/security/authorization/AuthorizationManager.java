package com.aleos.security.authorization;

import com.aleos.exception.context.AuthenticationException;
import com.aleos.exception.security.AccessDeniedException;
import com.aleos.http.CustomHttpSession;
import com.aleos.security.core.Authentication;
import com.aleos.security.core.GrantedAuthority;
import com.aleos.security.core.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AuthorizationManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

    private final Map<String, List<Role>> authorizationRules = new HashMap<>();

    public boolean check(HttpServletRequest req, Supplier<Authentication> authentication) {
        var auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationException("Not authenticated");
        }

        var reqUri = req.getRequestURI();
        List<Role> allowedRoles = authorizationRules.get(reqUri);

        if (allowedRoles == null) {
            String authorizationRulesError = "Authorization rules not founded to the target resource";
            logger.warn(authorizationRulesError);
            throw new AccessDeniedException("Access denied");
        }


        boolean authDecision = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(allowedRoles::contains);

        if (!authDecision && "anonymous".equals(auth.getPrincipal())) {
            setOriginalRequestInSession(req);
            throw new AuthenticationException("Authentication required");
        }

        return authDecision;
    }

    public void addRules(Map<String, List<Role>> rules) {
        authorizationRules.putAll(rules);
    }

    private static void setOriginalRequestInSession(HttpServletRequest req) {
        var session = (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
        session.setOriginalRequest(req.getRequestURI());
    }
}
