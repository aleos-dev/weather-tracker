package com.aleos.http;

import java.util.UUID;


public interface CustomHttpSession {

        String SESSION_CONTEXT_KEY = "SESSION_CONTEXT";

        long getCreationTime();

        UUID getId();

        Object getAttribute(String name);
        void setAttribute(String name, Object value);

        long getLastAccessedTime();
        void setLastAccessedTime(long lastAccessedTime);

        String getOriginalRequest();
        void setOriginalRequest(String originalRequest);

        void removeAttribute(String name);

        void invalidate();
}
