package com.aleos.http;

import com.aleos.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;


public interface CustomHttpSession {

        String SESSION_CONTEXT_KEY = "SESSION_CONTEXT";

        UUID getId();

        Object getAttribute(String name);

        void setAttribute(String name, Object value);

        long getLastAccessedTime();

        void setLastAccessedTime(long lastAccessedTime);

        String getOriginalRequest();

        void setOriginalRequest(String originalRequest);

        Optional<Authentication> getAuthentication();

        void setAuthentication(Authentication authentication);

        boolean isAuthenticated();

        String getPrincipal();

        void invalidate();
}
