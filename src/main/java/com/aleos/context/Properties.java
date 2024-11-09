package com.aleos.context;

import com.aleos.exception.context.PropertiesLoadingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Properties {

    private static final java.util.Properties props = new java.util.Properties();

    private static final String DB_URL_KEY = "WEATHER_TRACKER_DB_URL";
    private static final String DB_USER_KEY = "WEATHER_TRACKER_DB_USER";
    private static final String DB_PASSWORD_KEY = "WEATHER_TRACKER_DB_PASSWORD";

    private static final String SENDER_EMAIL = "WEATHER_TRACKER_MAIL_SERVICE_SENDER";
    private static final String EMAIL_SERVICE_CODE = "WEATHER_TRACKER_MAIL_SERVICE_CODE";

    private static final String WEATHER_API_KEY = "WEATHER_TRACKER_REMOTE_API_KEY";

    private static final String REDIS_HOST = "REDIS_HOST";
    private static final String REDIS_PORT = "REDIS_PORT";

    static {
        try (InputStream input = Properties.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                throw new PropertiesLoadingException("Properties file not found");
            }

            props.load(input);

            setPropertyFromEnvOrSystem(DB_URL_KEY);
            setPropertyFromEnvOrSystem(DB_USER_KEY);
            setPropertyFromEnvOrSystem(DB_PASSWORD_KEY);

            setPropertyFromEnvOrSystem(SENDER_EMAIL);
            setPropertyFromEnvOrSystem(EMAIL_SERVICE_CODE);

            setPropertyFromEnvOrSystem(WEATHER_API_KEY);

            setPropertyFromEnvOrSystem(REDIS_HOST);
            setPropertyFromEnvOrSystem(REDIS_PORT);

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

    private static void setPropertyFromEnvOrSystem(String key) {
        String property = System.getProperty(key) == null ? System.getenv(key) : System.getProperty(key);

        if (property == null) {
            throw new PropertiesLoadingException("Property for the key: %s cannot be find".formatted(key));
        }

        props.setProperty(key, property);
    }
}
