package com.aleos.exception.security;

public class UnauthenticatedAccessException extends RuntimeException {

    public UnauthenticatedAccessException(String message) {
        super(message);
    }
}
