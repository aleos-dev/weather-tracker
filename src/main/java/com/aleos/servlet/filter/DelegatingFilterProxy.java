package com.aleos.servlet.filter;

import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import com.aleos.security.web.SecurityFilterChain;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * A proxy class that delegates web requests to a configured security filter chain.
 * This class extends HttpFilter and serves as a bridge between the web application
 * and the security filter.
 * <p>
 * The DelegatingFilterProxy initializes the delegate filter by obtaining it from
 * the application's ServiceLocator during filter initialization. During request processing,
 * the proxy either delegates the request to the configured security filter or passes the
 * request to the next filter in the chain if no match is found.
 */
@Slf4j
public class DelegatingFilterProxy extends HttpFilter {

    private transient SecurityFilterChain delegateFilter;

    /**
     * Initializes the DelegatingFilterProxy by locating and setting the delegate security filter from the ServiceLocator.
     *
     * @param config the filter configuration provided by the servlet container,
     *               which includes the servlet context for accessing the ServiceLocator
     */
    @Override
    public void init(FilterConfig config) {
        log.info("Initializing DelegatingFilterProxy");

        var locator = (ServiceLocator) config.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        if (locator != null) {
            delegateFilter = locator.getBean(SecurityFilterChain.class);
            log.info("Delegate filter located and initialized: {}", delegateFilter.getClass().getSimpleName());
        } else {
            log.warn("ServiceLocator not found in ServletContext; delegate filter will not be set.");
        }
    }

    /**
     * Processes the incoming web request and delegates to the appropriate security filter,
     * or passes the request down the filter chain.
     *
     * @param req   the HttpServletRequest object that contains the client request
     * @param res   the HttpServletResponse object that contains the response for the client
     * @param chain the FilterChain for invoking the next filter or the resource
     * @throws ServletException if an exception has occurred that interferes with the filter's normal operation
     * @throws IOException      if an input or output exception occurs during processing
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        log.debug("Processing request: {} {}", req.getMethod(), req.getRequestURI());

        if (delegateFilter != null && delegateFilter.matches(req)) {
            log.info("Request matches delegate security filter; applying delegate security filter.");
            delegateFilter.apply(req, res, chain);
        } else {
            log.info("Request does not match delegate filter; passing request to next filter.");
            chain.doFilter(req, res);
        }
    }
}
