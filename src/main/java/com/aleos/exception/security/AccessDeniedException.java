package com.aleos.exception.security;

/**
 * The AccessDeniedException is a custom runtime exception that indicates
 * the user does not have permission to access the requested resource.
 * <p>
 * This exception is typically used in scenarios where authorization checks
 * fail, and it is essential to notify the application about the access denial.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
