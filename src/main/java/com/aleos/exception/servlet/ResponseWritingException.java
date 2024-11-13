package com.aleos.exception.servlet;

import java.io.IOException;

/**
 * This exception is thrown to indicate an error that occurs while writing a response.
 * <p>
 * Extends the RuntimeException class and provides a constructor to pass a detailed
 * message and the associated IOException that caused this exception.
 * <p>
 * The ResponseWritingException serves as a specific indication of I/O problems
 * encountered during the response writing process, encapsulating the underlying
 * IOException.
 */
public class ResponseWritingException extends RuntimeException {
    public ResponseWritingException(String message, IOException e) {
        super(message, e);
    }
}
