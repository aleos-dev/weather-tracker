package com.aleos.security.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DefaultSecurityFilterChain implements SecurityFilterChain {

    private final String pattern;

    private final List<Filter> filters;

    public DefaultSecurityFilterChain(String pattern, List<Filter> filters) {
        this.pattern = pattern;
        this.filters = filters;
        log.info("DefaultSecurityFilterChain created with pattern: {} and filters: {}", pattern, filters);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        boolean match = request.getRequestURI().startsWith(pattern);
        log.debug("Request URI: {} - Pattern: {} - Matches: {}", request.getRequestURI(), pattern, match);
        return match;
    }

    @Override
    public void apply(HttpServletRequest request, HttpServletResponse response, FilterChain originalChain)
            throws ServletException, IOException {
        FilterChainImpl customChain = new FilterChainImpl(filters, originalChain);
        customChain.doFilter(request, response);
    }

    @Override
    public String toString() {
        return "DefaultSecurityFilterChain{" +
               "pattern='" + pattern + '\'' +
               ", filters=" + filters +
               '}';
    }

    private static class FilterChainImpl implements FilterChain {

        private final List<Filter> filters;
        private final FilterChain originalChain;
        private int currentPosition = 0;

        public FilterChainImpl(List<Filter> filters, FilterChain originalChain) {
            this.filters = filters;
            this.originalChain = originalChain;
        }

        @Override
        public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
            if (currentPosition < filters.size()) {
                Filter nextFilter = filters.get(currentPosition++);
                log.debug("Applying filter: {} - Position: {}", nextFilter, currentPosition);
                nextFilter.doFilter(req, res, this);
            } else {
                log.debug("All custom filters applied, proceeding with the original filter chain.");
                originalChain.doFilter(req, res);
            }
        }
    }
}
