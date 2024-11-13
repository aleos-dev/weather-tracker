package com.aleos.exception.security;


/**
 * Exception thrown when a specified resource is not found.
 * <p>
 * This runtime exception is typically used to indicate that a requested
 * resource cannot be found in the application, such as when an authorization
 * rule is not available for a requested URI.
 * <p>
 * The exception can be constructed with a custom message to provide more
 * context about the missing resource.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
