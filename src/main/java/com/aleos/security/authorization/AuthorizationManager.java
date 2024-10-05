package com.aleos.security.authorization;

import com.aleos.security.core.Authentication;
import com.aleos.security.core.GrantedAuthority;
import com.aleos.security.core.Role;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AuthorizationManager {

    private final Map<String, List<Role>> authorizationRules = new HashMap<>();

    public boolean check(HttpServletRequest req, Supplier<Authentication> authentication) {
        var auth = authentication.get();

        if (auth != null && auth.isAuthenticated()) {

            var reqUri = req.getRequestURI();
            List<Role> allowedRoles = authorizationRules.get(reqUri);

            // if not specified, access allowed
            if (allowedRoles == null) {
                return true;
            }

            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(allowedRoles::contains);
        }

        return false;
    }

    public void addRules(Map<String, List<Role>> rules) {
        authorizationRules.putAll(rules);
    }
}
