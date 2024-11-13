package com.aleos.exception.context;

/**
 * Exception thrown when there is an issue loading properties.
 */
public class PropertiesLoadingException extends RuntimeException {

    public PropertiesLoadingException(String message, Exception e) {
        super(message, e);
    }

    public PropertiesLoadingException(String message) {
        super(message);
    }
}
