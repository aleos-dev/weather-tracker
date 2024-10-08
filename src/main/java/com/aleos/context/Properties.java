package com.aleos.context;

import com.aleos.exception.context.PropertiesLoadingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public final class Properties {

    private static final java.util.Properties props = new java.util.Properties();

    static {
        try (InputStream input = Properties.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                throw new PropertiesLoadingException("Properties file not found");
            }

            props.load(input);

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
