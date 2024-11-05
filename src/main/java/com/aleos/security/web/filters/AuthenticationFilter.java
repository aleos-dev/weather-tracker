package com.aleos.security.web.filters;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.exception.security.InvalidSessionException;
import com.aleos.http.CustomHttpSession;
import com.aleos.model.ErrorDetails;
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
import java.util.Optional;

@AllArgsConstructor
public class AuthenticationFilter extends HttpFilter {

    private static final String USERNAME_PARAM = "name";
    private static final String PASSWORD_PARAM = "password";

    private static final String AUTH_METHOD = "POST";
    private static final String AUTH_URI = Properties.get("auth.url").orElse("/api/v1/sign-in");

    private final transient AuthenticationService authenticationService;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        Authentication auth = restoreAuthenticationFromSession(req);

        if (isNotAuthenticated(auth) && isAuthRequest(req)) {
            var username = req.getParameter(USERNAME_PARAM);
            var password = req.getParameter(PASSWORD_PARAM);

            try {

                authenticateUser(username, password);
                saveAuthenticationToSession(req);

            } catch (AuthenticationException e) {
                //logger
                setOriginalRequestInSession(req);
                req.setAttribute("errors", ErrorDetails.fromSingleError(e.getMessage()));
                req.getRequestDispatcher(AUTH_URI).forward(req, res);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private Authentication restoreAuthenticationFromSession(HttpServletRequest req) {
        var session = getCustomHttpSession(req);
        Optional<Authentication> auth = session.getAuthentication();
        auth.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        return auth.orElse(null);
    }

    private CustomHttpSession getCustomHttpSession(HttpServletRequest req) {
        Object attribute = req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY);
        if (attribute instanceof CustomHttpSession session) {
            return session;
        }
        throw new InvalidSessionException("The session is null or has wrong type");
    }

    private static boolean isNotAuthenticated(Authentication auth) {
        return auth == null || auth.isAnonymous() || !auth.setAuthenticated();
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

    private void saveAuthenticationToSession(HttpServletRequest req) {
        getCustomHttpSession(req).setAuthentication(SecurityContextHolder.getContext().getAuthentication());
    }
}
