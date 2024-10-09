package com.aleos.exception.servlet;

import java.io.IOException;

public class ResponseWritingException extends RuntimeException {
    public ResponseWritingException(String message, IOException e) {
        super(message, e);
    }
}
