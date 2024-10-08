package com.aleos.exception.security;

public class BeanInitializationException extends RuntimeException {

    public BeanInitializationException(String message) {
        super(message);
    }

    public BeanInitializationException(String message, Exception cause) {
        super(message, cause);
    }
}
