package com.aleos.security.web.filters;

import com.aleos.exception.security.AccessDeniedException;
import com.aleos.security.authorization.AuthorizationManager;
import com.aleos.security.core.Authentication;
import com.aleos.security.web.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class AuthorizationFilter extends HttpFilter {

    private final transient AuthorizationManager authorizationManager;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        boolean isPermitted = authorizationManager.check(req, this::getAuthentication);
        if (!isPermitted) {
            throw new AccessDeniedException("Access Denied");
        }

        chain.doFilter(req, res);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
