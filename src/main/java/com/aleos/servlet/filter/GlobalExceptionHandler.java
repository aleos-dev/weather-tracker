package com.aleos.servlet.filter;

import com.aleos.exception.service.WeatherClientException;
import com.aleos.exception.repository.UniqueConstraintViolationException;
import com.aleos.exception.security.ResourceNotFoundException;
import com.aleos.exception.servlet.CoordinateParsingException;
import com.aleos.model.ErrorDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.aleos.servlet.AbstractThymeleafServlet.ERROR_ATTRIBUTE_KEY;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * A global exception handler filter for managing and logging exceptions
 * occurring during HTTP request processing.
 * <p>
 * This class extends {@link HttpFilter} and overrides the {@code doFilter} method
 * to intercept requests and handle various types of exceptions.
 */
@Slf4j
public class GlobalExceptionHandler extends HttpFilter {

    /**
     * Intercepts HTTP requests and processes them through the filter chain.
     * Handles various types of exceptions that may occur during request processing,
     * and sets appropriate status codes and error messages in the response.
     *
     * @param req the HttpServletRequest object that contains the request the client made to the servlet
     * @param res the HttpServletResponse object that contains the response the servlet returns to the client
     * @param chain the FilterChain for invoking the next filter or the resource
     * @throws IOException if an input or output error occurs while the filter is processing the request
     * @throws ServletException if the request could not be handled
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (UniqueConstraintViolationException e) {
            handleException(req, res, e, "Unique constraint violation.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (WeatherClientException e) {
            handleException(req, res, e, "Weather client exception.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (CoordinateParsingException e) {
            handleException(req, res, e, "Coordinates have wrong format. Aborting request.", HttpServletResponse.SC_BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            handleException(req, res, e, "Resource not found.", SC_NOT_FOUND);
        } catch (Exception e) {
            handleException(req, res, e, "Global exception occurred.", SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(HttpServletRequest req, HttpServletResponse res, Exception e, String logMessage, int statusCode) throws IOException, ServletException {
        switch (statusCode) {
            case SC_INTERNAL_SERVER_ERROR -> log.error(logMessage, e);
            case SC_NOT_FOUND -> log.warn(logMessage, e);
            default -> log.info(logMessage, e);
        }

        setErrorDetails(req, logMessage);
        if (statusCode == SC_NOT_FOUND || statusCode == SC_INTERNAL_SERVER_ERROR) {
            res.sendError(statusCode);
        } else {
            // mem: forward to the same URI may cause infinite loop
            req.getRequestDispatcher(req.getRequestURI()).forward(req, res);
        }
    }

    private void setErrorDetails(HttpServletRequest req, String message) {
        req.setAttribute(ERROR_ATTRIBUTE_KEY, ErrorDetails.fromSingleError(message));
    }
}