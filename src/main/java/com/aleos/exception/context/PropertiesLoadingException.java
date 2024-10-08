package com.aleos.exception.context;

public class PropertiesLoadingException extends RuntimeException {

    public PropertiesLoadingException(String message, Exception e) {
        super(message, e);
    }

    public PropertiesLoadingException(String message) {
        super(message);
    }
}
