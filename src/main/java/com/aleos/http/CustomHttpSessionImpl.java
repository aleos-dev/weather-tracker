package com.aleos.http;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CustomHttpSessionImpl implements CustomHttpSession {

    private final UUID id;

    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    private final long creationTime;

    private long lastAccessedTime;

    public CustomHttpSessionImpl(UUID id) {
        this.id = id;
        creationTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
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
