package com.aleos.service;

import com.aleos.exception.context.AuthenticationException;
import com.aleos.security.core.Authentication;

/**
 * Service interface for handling authentication-related operations.
 * <p>
 * Methods:
 * - `authenticate(String username, String password)`: Authenticates a user using provided username and password.
 * <p>
 * Exceptions:
 * - `AuthenticationException`: Thrown when authentication fails due to invalid credentials or other reasons.
 */
public interface AuthenticationService {

    Authentication authenticate(String username, String password) throws AuthenticationException;
}
