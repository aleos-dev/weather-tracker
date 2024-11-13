package com.aleos.exception.repository;

/**
 * Exception thrown to indicate a problem with a DAO operation.
 * <p>
 * This exception is a subclass of {@link RuntimeException} and is intended
 * to be used for wrapping underlying exceptions that occur during data access
 * operations. It can be used to provide a consistent exception handling mechanism
 * while abstracting the underlying cause.
 * <p>
 * The {@code DaoOperationException} class provides a constructor that takes a detailed
 * error message and the underlying exception that caused the DAO operation to fail.
 */
public class DaoOperationException extends RuntimeException {

    public DaoOperationException(String message, Exception e) {
        super(message, e);
    }
}
