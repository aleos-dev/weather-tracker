package com.aleos.servlet.filter;

import com.aleos.exception.WeatherClientException;
import com.aleos.exception.repository.UniqueConstraintViolationException;
import com.aleos.exception.security.ResourceNotFoundException;
import com.aleos.model.ErrorDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.aleos.servlet.AbstractThymeleafServlet.ERROR_ATTRIBUTE_KEY;

public class GlobalExceptionHandler extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);

        } catch (UniqueConstraintViolationException e) {
            handleSpecificException(req, res, e, "Unique constraint violation.");
        } catch (WeatherClientException e) {
            handleSpecificException(req, res, e, "Weather client exception");
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found.", e);
            res.sendError(404);
        } catch (Exception e) {
            logger.warn("Global exception occurred.", e);
            res.sendError(500);
        }
    }

    private void handleSpecificException(HttpServletRequest req, HttpServletResponse res, Exception e, String logMessage) throws ServletException, IOException {
        logger.warn(logMessage, e);
        setErrorDetails(req, logMessage);
        req.getRequestDispatcher(req.getRequestURI()).forward(req, res);
    }

    private void setErrorDetails(HttpServletRequest req, String message) {
        req.setAttribute(ERROR_ATTRIBUTE_KEY, ErrorDetails.fromSingleError(message));
    }
}