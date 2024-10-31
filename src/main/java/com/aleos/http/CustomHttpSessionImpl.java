package com.aleos.http;

import com.aleos.security.core.Authentication;
import com.aleos.security.core.AuthenticationToken;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@JsonIgnoreProperties({"authentication", "authenticated", "principal"})
public class CustomHttpSessionImpl implements CustomHttpSession {

    public static final String AUTH_SESSION_KEY = "AUTHENTICATION";

    @JsonProperty
    private final ConcurrentMap<String, Serializable> attributes = new ConcurrentHashMap<>();

    @Getter
    private final UUID id;

    @Getter
    @Setter
    private long lastAccessedTime;

    @Getter
    @Setter
    private String originalRequest;

    @JsonCreator
    public CustomHttpSessionImpl(@JsonProperty("id") UUID id) {
        this.id = id;
        lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public Serializable getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Serializable value) {
        attributes.put(key, value);
    }

    @Override
    public Serializable removeAttribute(String key) {
        return attributes.remove(key);
    }

    @Override
    public Optional<Authentication> getAuthentication() {
        Serializable auth = attributes.get(AUTH_SESSION_KEY);
        return auth == null
                ? Optional.empty()
                : Optional.of((Authentication) auth);
    }

    @Override
    public void setAuthentication(Authentication auth) {
        if (auth != null) {
            attributes.put(AUTH_SESSION_KEY, auth);
        }
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
