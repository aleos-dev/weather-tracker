package com.aleos.exception.service;

/**
 * Exception thrown when an error occurs while parsing a Data Transfer Object (DTO).
 * This exception extends RuntimeException, indicating that it is unchecked and can
 * be thrown during the normal operation of the Java Virtual Machine.
 */
public class ParseDtoException extends RuntimeException {

    public ParseDtoException(String message, Exception e) {
        super(message, e);
    }

    public ParseDtoException(String message) {
        super(message);
    }
}
