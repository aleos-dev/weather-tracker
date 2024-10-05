package com.aleos.util;

import com.aleos.exception.PropertiesLoadingException;

import java.io.IOException;
import java.util.Optional;

public final class Properties {

    private static final java.util.Properties props = new java.util.Properties();

    static {
        try {
            props.load(Properties.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            throw new PropertiesLoadingException("Failed to load properties file", e);
        }
    }

    private Properties() {
        throw new UnsupportedOperationException("Util class can not be instantiated");
    }

    public static Optional<String> get(String key) {
        return Optional.ofNullable(props.getProperty(key));
    }
}
