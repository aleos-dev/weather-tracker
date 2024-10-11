package com.aleos.exception.service;

public class ParseDtoException extends RuntimeException {

    public ParseDtoException(String message, Exception e) {
        super(message, e);
    }

    public ParseDtoException(String message) {
        super(message);
    }
}
