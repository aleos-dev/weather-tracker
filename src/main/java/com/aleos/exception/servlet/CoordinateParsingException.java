package com.aleos.exception.servlet;

public class CoordinateParsingException extends RuntimeException {

    public CoordinateParsingException(String message, Exception e) {
        super(message, e);
    }

    public CoordinateParsingException(String message) {
        super(message);
    }
}
