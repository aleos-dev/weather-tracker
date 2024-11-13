package com.aleos.exception.servlet;

/**
 * Exception thrown when there is an error while parsing coordinate values.
 * This exception can be used to indicate that the provided longitude or latitude
 * value is not in the correct format or cannot be parsed into a valid coordinate.
 */
public class CoordinateParsingException extends RuntimeException {

    public CoordinateParsingException(String message, Exception e) {
        super(message, e);
    }

    public CoordinateParsingException(String message) {
        super(message);
    }
}
