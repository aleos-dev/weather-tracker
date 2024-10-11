package com.aleos.servlet.filter;

import com.aleos.context.Properties;
import com.aleos.exception.repository.UniqueConstraintViolationException;
import com.aleos.exception.security.ResourceNotFoundException;
import com.aleos.model.ErrorData;
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

    public static final String ERROR_PAGE_URL = Properties.get("error.url").orElseThrow();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (UniqueConstraintViolationException e) {
            logger.debug("Unique constrain violation: ", e);
            req.setAttribute("errorData", ErrorData.fromSingleError(e.getMessage()));
            req.getRequestDispatcher(req.getRequestURI()).forward(req, res);
        } catch (ResourceNotFoundException e) {
            logger.debug("Resource not found: {}", e.getMessage());
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.debug("Unknown error", e);
            req.setAttribute("errorData", "Unknown error");
            req.getRequestDispatcher(ERROR_PAGE_URL).forward(req, res);
        }
    }
}

