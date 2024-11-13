package com.aleos.exception.context;

/**
 * Exception thrown when an authentication-related error occurs.
 * <p>
 * This runtime exception is typically used to indicate that a user
 * is not authenticated or that authentication is required for accessing
 * a specific resource.
 * <p>
 * Instances of this exception are often thrown during the authorization
 * process when the user's authentication status does not meet the
 * required criteria for certain operations.
 * <p>
 * The exception is constructed with a message to provide more context
 * about the authentication failure.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
