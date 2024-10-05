package com.aleos.servlet.filter;

import com.aleos.security.web.SecurityFilterChain;
import com.aleos.servicelocator.BeanFactory;
import com.aleos.servicelocator.ServiceLocator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DelegatingFilterProxy extends HttpFilter {

    private transient SecurityFilterChain delegateFilter;

    @Override
    public void init(FilterConfig config) {
        var locator = (ServiceLocator) config.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        delegateFilter = locator.getBean(SecurityFilterChain.class);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if (delegateFilter != null && delegateFilter.matches(req)) {
            delegateFilter.apply(req, res, chain);
        } else {
            chain.doFilter(req, res);
        }
    }
}
