package com.aleos.exception.servlet;

import java.io.IOException;

/**
 * Exception thrown to indicate that a redirection has occurred during an I/O operation.
 * This is a custom runtime exception that wraps an {@link IOException}.
 * <p>
 * The RedirectException class extends {@link RuntimeException}, allowing it to be thrown
 * without requiring explicit handling (checked exception). It provides a constructor
 * that takes a descriptive message and the underlying {@link IOException} causing the redirection.
 */
public class RedirectException extends RuntimeException {

    public RedirectException(String message, IOException e) {
        super(message, e);
    }
}
