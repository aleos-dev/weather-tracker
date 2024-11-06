package com.aleos.exception;

public class WeatherClientException extends RuntimeException {

    public WeatherClientException(String message) {
        super(message);
    }

    public WeatherClientException(String message, Exception e) {
        super(message, e);
    }
}
