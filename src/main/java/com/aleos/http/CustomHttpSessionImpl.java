package com.aleos.http;

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
    private final long creationTime;

    @Getter
    @Setter
    private long lastAccessedTime;

    @Getter
    @Setter
    private String originalRequest;

    public CustomHttpSessionImpl(UUID id) {
        this.id = id;
        creationTime = System.currentTimeMillis();
        lastAccessedTime = creationTime;
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
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        attributes.clear();
    }
}
