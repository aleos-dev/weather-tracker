package com.aleos.exception.servlet;

import java.io.IOException;

public class RedirectException extends RuntimeException {

    public RedirectException(String message, IOException e) {
        super(message, e);
    }
}
