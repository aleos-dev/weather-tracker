package com.aleos.http;

import com.aleos.security.core.Authentication;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Implementation of the CustomHttpSession interface providing session management.
 * Stores session attributes and handles authentication logic. The session attributes
 * are stored in a thread-safe ConcurrentMap.
 */
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

    /**
     * Constructs a new instance of CustomHttpSessionImpl with the given session ID.
     * Initializes the session with the current time as the last accessed time.
     *
     * @param id the unique identifier for the session.
     */
    @JsonCreator
    public CustomHttpSessionImpl(@JsonProperty("id") UUID id) {
        this.id = id;
        lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * Retrieves the attribute associated with the given key from the session.
     *
     * @param key the name of the attribute to retrieve
     * @return the attribute value associated with the specified key, or null if no attribute is found
     */
    @Override
    public Serializable getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Sets a session attribute identified by a key to a given value.
     *
     * @param key   the name of the session attribute to be set
     * @param value the value of the session attribute to be set
     */
    @Override
    public void setAttribute(String key, Serializable value) {
        attributes.put(key, value);
    }

    /**
     * Removes an attribute from the session and returns the value of the removed attribute.
     *
     * @param key the key associated with the attribute to remove
     * @return the previous value associated with the specified key, or null if there was no mapping for the key
     */
    @Override
    public Serializable removeAttribute(String key) {
        return attributes.remove(key);
    }

    /**
     * Retrieves the current authentication object from the session if present.
     *
     * @return an Optional containing the Authentication object if it exists, otherwise empty
     */
    @Override
    public Optional<Authentication> getAuthentication() {
        Serializable auth = attributes.get(AUTH_SESSION_KEY);
        return auth == null
                ? Optional.empty()
                : Optional.of((Authentication) auth);
    }

    /**
     * Sets the authentication object in the session attributes. If the provided
     * authentication object is not null, it stores it in the session with the key
     * designated by {@code AUTH_SESSION_KEY}.
     *
     * @param auth the authentication object to be stored in the session, if not null
     */
    @Override
    public void setAuthentication(Authentication auth) {
        if (auth != null) {
            attributes.put(AUTH_SESSION_KEY, auth);
        }
    }

    /**
     * Checks if the current session is authenticated by verifying if the `Authentication` object
     * associated with this session is present and authenticated.
     *
     * @return true if the session has an authenticated `Authentication` object, false otherwise.
     */
    @Override
    public boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * Retrieves the principal from the current authentication context.
     *
     * @return the principal if present in the current authentication context, otherwise null
     */
    @Override
    public String getPrincipal() {
        return getAuthentication().map(Authentication::getPrincipal).orElse(null);
    }

    /**
     * Invalidates the current session by clearing all attributes and setting the last accessed time to zero.
     * This effectively renders the session invalid, removing any stored data associated with it.
     */
    @Override
    public void invalidate() {
        attributes.clear();
        lastAccessedTime = 0;
    }
}
