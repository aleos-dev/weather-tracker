package com.aleos.exception.service;

public class EmailServiceException extends RuntimeException {

    public EmailServiceException(String message, Exception e) {
        super(message, e);
    }
}
