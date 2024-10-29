package com.aleos.http;

import com.aleos.security.web.context.HttpSessionSecurityContextRepository;
import com.aleos.security.web.context.SecurityContext;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomHttpSessionImpl implements CustomHttpSession {

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
    public boolean isAuthenticated() {
        var context =
                (SecurityContext) attributes.get(HttpSessionSecurityContextRepository.SECURITY_CONTEXT_KEY);

        return context != null && context.isAuthenticated();
    }

    @Override
    public String getPrincipal() {
        var context =
                (SecurityContext) attributes.get(HttpSessionSecurityContextRepository.SECURITY_CONTEXT_KEY);

        return isAuthenticated()
                ? context.getAuthentication().getPrincipal()
                : null;
    }

    @Override
    public void invalidate() {
        attributes.clear();
        lastAccessedTime = 0;
    }
}
