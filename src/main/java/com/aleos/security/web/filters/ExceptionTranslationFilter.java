package com.aleos.security.web.filters;

import com.aleos.context.Properties;
import com.aleos.exception.context.AuthenticationException;
import com.aleos.exception.security.AccessDeniedException;
import com.aleos.security.web.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ExceptionTranslationFilter is a custom filter that handles specific types of exceptions
 * occurring during the filtering process in an HTTP request-response cycle.
 * <p>
 * This class extends HttpFilter and is designed to catch and process AuthenticationException
 * and AccessDeniedException. Depending on the type of exception, it either redirects the
 * user to a login page or sends an HTTP 403 "Access Denied" error.
 */
public class ExceptionTranslationFilter extends HttpFilter {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionTranslationFilter.class);

    private static final String REDIRECT_URL_KEY = Properties.get("auth.url").orElse("/api/v1/login");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);

        } catch (AuthenticationException e) {
            logger.debug("Redirecting to login page... {}", REDIRECT_URL_KEY);
            res.sendRedirect(REDIRECT_URL_KEY);

        } catch (AccessDeniedException e) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                logger.debug("User not have access to resource");
                res.sendError(403, "Access Denied");
            } else {
                logger.debug("Required authentication {}", authentication);
                res.sendRedirect(REDIRECT_URL_KEY);
            }
        }
    }
}
