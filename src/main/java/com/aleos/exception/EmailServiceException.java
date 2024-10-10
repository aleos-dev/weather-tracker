package com.aleos.exception;

public class EmailServiceException extends RuntimeException {

    public EmailServiceException(String message, Exception e) {
        super(message, e);
    }
}
