package com.aleos.security.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The SecurityFilterChain interface defines a contract for matching and applying security filters
 * to HTTP requests within a web application.
 */
public interface SecurityFilterChain {

    /**
     * Determines if the given HTTP request matches the criteria defined by the SecurityFilterChain.
     *
     * @param request the HttpServletRequest object that contains the client request
     * @return true if the request matches the criteria, false otherwise
     */
    boolean matches(HttpServletRequest request);

    /**
     * Applies the security filter to an incoming HTTP request and response.
     *
     * @param req   the HttpServletRequest object that contains the client request
     * @param res   the HttpServletResponse object that contains the response for the client
     * @param chain the FilterChain for invoking the next filter or resource
     * @throws ServletException if an exception occurs that interferes with the filter's normal operation
     * @throws IOException      if an input or output exception occurs during processing
     */
    void apply(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException;
}
