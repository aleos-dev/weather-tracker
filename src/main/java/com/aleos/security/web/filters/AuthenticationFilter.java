package com.aleos.security.web.filters;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.http.CustomHttpSession;
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
    private static final String AUTH_URI = Properties.get("auth.url").orElse("/api/v1/login");

    private final transient AuthenticationService authenticationService;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isNotAuthenticated(auth) && isAuthRequest(req)) {
            var username = req.getParameter(USERNAME_PARAM);
            var password = req.getParameter(PASSWORD_PARAM);

            try {
                authenticateUser(username, password);

            } catch (AuthenticationException e) {
                setOriginalRequestInSession(req);
                res.sendRedirect(AUTH_URI);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private static boolean isNotAuthenticated(Authentication auth) {
        return auth == null || auth.isAnonymous() || !auth.isAuthenticated();
    }

    private static boolean isAuthRequest(HttpServletRequest req) {
        return req.getMethod().equalsIgnoreCase(AUTH_METHOD) && req.getRequestURI().equals(AUTH_URI);
    }

    private static void setOriginalRequestInSession(HttpServletRequest req) {
        var session = (CustomHttpSession) req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
        if (session.getOriginalRequest() == null) {
            session.setOriginalRequest(req.getRequestURI());
        }
    }

    private void authenticateUser(String username, String password) {
        var authenticationToken = authenticationService.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
