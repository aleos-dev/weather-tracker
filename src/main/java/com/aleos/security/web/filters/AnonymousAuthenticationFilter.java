package com.aleos.security.web.filters;

import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.aleos.security.core.Role;
import com.aleos.security.core.SimpleGrantedAuthority;
import com.aleos.security.util.SingletonSupplier;
import com.aleos.security.web.context.SecurityContext;
import com.aleos.security.web.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * A filter that ensures a user is authenticated anonymously if no existing authentication is present.
 * <p>
 * This filter checks the current security context for an existing authentication. If none is found,
 * it sets an anonymous authentication context.
 */
public class AnonymousAuthenticationFilter extends HttpFilter {

    public static final String ANONYMOUS_USER = "Anonymous";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            var currentContext = SecurityContextHolder.getContext();
            SecurityContextHolder.setDeferredContext(defaultWithAnonymous(currentContext));
        }

        chain.doFilter(req, res);
    }

    private Supplier<SecurityContext> defaultWithAnonymous(SecurityContext currentContext) {
        Authentication auth = currentContext.getAuthentication();

        if (auth == null) {
            Authentication anonymous = createAuthentication();
            SecurityContext anonymousContext = SecurityContextHolder.createEmptyContext();
            anonymousContext.setAuthentication(anonymous);
            return SingletonSupplier.of(() -> anonymousContext);
        }

        return SingletonSupplier.of(() -> currentContext);
    }

    private Authentication createAuthentication() {
        var authToken = new AuthenticationToken(ANONYMOUS_USER, new SimpleGrantedAuthority(Role.ANONYMOUS));
        authToken.isAuthenticated(false);
        return authToken;
    }
}
