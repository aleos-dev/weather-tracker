package com.aleos.security.core;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an authentication request or response.
 * <p>
 * This interface is intended to provide a standard way to handle authentication-related data
 * within security contexts.
 */
public interface Authentication extends Serializable {

    String getPrincipal();

    List<GrantedAuthority> getAuthorities();

    boolean isAuthenticated();

    void isAuthenticated(boolean isAuthenticated);

    boolean isAnonymous();
}
