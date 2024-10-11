package com.aleos.exception.repository;

public class DaoOperationException extends RuntimeException {

    public DaoOperationException(String message, Exception e) {
        super(message, e);
    }
}
