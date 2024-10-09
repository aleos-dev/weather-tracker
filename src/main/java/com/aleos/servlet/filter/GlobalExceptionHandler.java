package com.aleos.servlet.filter;

import com.aleos.context.Properties;
import com.aleos.exception.security.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GlobalExceptionHandler extends HttpFilter {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static final String BASE_REDIRECT_URL = Properties.get("base.url").orElse("/api/v1/welcome");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (ResourceNotFoundException e) {
            logger.debug("Redirect to: {}", BASE_REDIRECT_URL, e);
            res.sendRedirect(BASE_REDIRECT_URL);
        } catch (Exception e) {
            logger.debug("Unknown error", e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

