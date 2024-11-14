package com.aleos.exception.security;

/**
 * Exception thrown to indicate that a session is invalid or has an incorrect type.
 * <p>
 * This exception is typically thrown when a session object retrieved from a request
 * does not meet the expected type or is null, indicating an invalid state for operation.
 *
 * @see RuntimeException
 */
public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException(String s) {
        super(s);
    }
}
