package com.aleos.exception.service;

/**
 * Exception indicating a problem with the weather client.
 * <p>
 * This class extends {@link RuntimeException} and can be used to signal
 * issues specifically related to the weather client operations. It supports
 * two constructors: one that accepts only a message, and another that
 * accepts both a message and a causing exception.
 */
public class WeatherClientException extends RuntimeException {

    public WeatherClientException(String message) {
        super(message);
    }

    public WeatherClientException(String message, Exception e) {
        super(message, e);
    }
}
