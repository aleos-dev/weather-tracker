package com.aleos.security.configuration;

import com.aleos.security.web.DefaultSecurityFilterChain;
import jakarta.servlet.Filter;

import java.util.ArrayList;
import java.util.List;

public class SecurityFilterChainConfigurer {

    private String pattern;

    private final List<Filter> filters = new ArrayList<>();

    public SecurityFilterChainConfigurer addFilter(Filter filter) {
        this.filters.add(filter);
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
