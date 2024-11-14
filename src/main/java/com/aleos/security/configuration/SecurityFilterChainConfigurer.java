package com.aleos.security.configuration;

import com.aleos.security.web.DefaultSecurityFilterChain;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * The SecurityFilterChainConfigurer class is responsible for configuring a security filter chain.
 * It provides methods to add filters, set URL patterns, and build the filter chain.
 */
@Slf4j
public class SecurityFilterChainConfigurer {

    private String pattern;

    private final List<Filter> filters = new ArrayList<>();

    public SecurityFilterChainConfigurer addFilter(Filter filter) {
        if (filter == null) {
            log.error("Attempted to add a null filter.");
            throw new IllegalArgumentException("Filter cannot be null");
        }

        this.filters.add(filter);
        log.info("Added Security Filter: {}", filter.getClass().getSimpleName());

        return this;
    }

    public SecurityFilterChainConfigurer addFilter(Filter filter, boolean isEnabled) {
        if (isEnabled) {
            return addFilter(filter);
        }
        return this;
    }

    public SecurityFilterChainConfigurer setPattern(String pattern) {
        this.pattern = pattern;

        return this;
    }

    public DefaultSecurityFilterChain build() {
        return new DefaultSecurityFilterChain(pattern, filters);
    }
}
