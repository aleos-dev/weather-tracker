package com.aleos.http;

import com.aleos.security.core.Authentication;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomHttpSessionImpl implements CustomHttpSession {

    private static final String AUTH_SESSION_KEY = "AUTHENTICATION";

    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    @Getter
    private final UUID id;

    @Getter
    @Setter
    private long lastAccessedTime;

    @Getter
    @Setter
    private String originalRequest;

    public CustomHttpSessionImpl(UUID id) {
        this.id = id;
        lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Optional<Authentication> getAuthentication() {
        return Optional.ofNullable((Authentication) attributes.get(AUTH_SESSION_KEY));
    }

    @Override
    public void setAuthentication(Authentication auth) {
        attributes.put(AUTH_SESSION_KEY, auth);
    }

    @Override
    public boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    @Override
    public String getPrincipal() {
        return getAuthentication().map(Authentication::getPrincipal).orElse(null);
    }

    @Override
    public void invalidate() {
        attributes.clear();
        lastAccessedTime = 0;
    }
}
