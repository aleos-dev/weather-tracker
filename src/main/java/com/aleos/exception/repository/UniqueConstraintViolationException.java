package com.aleos.exception.repository;

/**
 * Exception thrown when a unique constraint is violated in the database.
 * <p>
 * This exception extends {@link RuntimeException} and is typically thrown
 * when attempting to insert or update a record that would result in a
 * violation of a uniqueness constraint on a database column or combination
 * of columns.
 * <p>
 * The {@code UniqueConstraintViolationException} class provides a constructor that
 * takes a detailed error message and the underlying exception that caused
 * the unique constraint violation.
 */
public class UniqueConstraintViolationException extends RuntimeException {

    public UniqueConstraintViolationException(String message, Exception e) {
        super(message, e);
    }
}
