package com.aleos.exception.service;

/**
 * The EmailServiceException class is a custom exception used in the context of email services.
 * It extends the RuntimeException, indicating that it is an unchecked exception.
 * This exception should be thrown to signal issues encountered when dealing with email operations.
 * <p>
 * The exception includes a constructor that takes a detailed message and a nested exception
 * that caused this exception to be thrown.
 */
public class EmailServiceException extends RuntimeException {

    public EmailServiceException(String message, Exception e) {
        super(message, e);
    }
}
