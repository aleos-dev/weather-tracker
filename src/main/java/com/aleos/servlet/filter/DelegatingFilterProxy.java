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

@Slf4j
public class DelegatingFilterProxy extends HttpFilter {

    private transient SecurityFilterChain delegateFilter;

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
