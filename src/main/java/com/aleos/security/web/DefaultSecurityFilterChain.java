package com.aleos.security.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class DefaultSecurityFilterChain implements SecurityFilterChain {

    private final String pattern;

    private final List<Filter> filters;

    public DefaultSecurityFilterChain(String pattern, List<Filter> filters) {
        this.pattern = pattern;
        this.filters = filters;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return request.getRequestURI().startsWith(pattern);
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
                nextFilter.doFilter(req, res, this);
            } else {
                originalChain.doFilter(req, res);
            }
        }
    }
}
