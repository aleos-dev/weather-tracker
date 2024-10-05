package com.aleos.security.web.filters;

import com.aleos.security.core.Authentication;
import com.aleos.security.web.context.SecurityContextHolder;
import com.aleos.service.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class AuthenticationFilter extends HttpFilter {

    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";

    private static final String AUTH_METHOD = "POST";
    private static final String AUTH_URI = "/login";

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            handleUnauthenticatedRequest(req, res);
        }

        chain.doFilter(req, res);
    }

    private void handleUnauthenticatedRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (isAuthRequest(req)) {
            var username = req.getParameter(USERNAME_PARAM);
            var password = req.getParameter(PASSWORD_PARAM);

            if (username == null || password == null) {
                res.sendRedirect(AUTH_URI);
            }

            authenticateUser(username, password);
        }
    }

    private static boolean isAuthRequest(HttpServletRequest req) {
        return req.getMethod().equalsIgnoreCase(AUTH_METHOD) && req.getRequestURI().equals(AUTH_URI);
    }

    private void authenticateUser(String username, String password) {
        var authenticationToken = authenticationService.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
