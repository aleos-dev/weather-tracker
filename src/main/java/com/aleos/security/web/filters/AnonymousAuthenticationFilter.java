package com.aleos.security.web.filters;

import com.aleos.security.core.*;
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

public class AnonymousAuthenticationFilter extends HttpFilter {

    private static final String ANONYMOUS_USER = "anonymous";
    private static final String ANONYMOUS_PASS = "anonymous";

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
        return new AuthenticationToken(ANONYMOUS_USER, ANONYMOUS_PASS, new SimpleGrantedAuthority(Role.ANONYMOUS));
    }
}
