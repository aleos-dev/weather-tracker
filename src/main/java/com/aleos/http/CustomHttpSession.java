package com.aleos.http;

import com.aleos.security.core.Authentication;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface representing a custom HTTP session.
 * <p>
 * A CustomHttpSession provides mechanisms to manage session attributes,
 * track authentication status, and manage session lifecycle events such
 * as invalidation and access time updates.
 */
public interface CustomHttpSession extends Serializable {

        String SESSION_CONTEXT_KEY = "SESSION_CONTEXT";

        UUID getId();

        Serializable getAttribute(String key);

        void setAttribute(String key, Serializable value);

        Serializable removeAttribute(String key);

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
