package com.aleos.context;

import com.aleos.exception.context.PropertiesLoadingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Utility class for managing application properties.
 * <p>
 * This class loads properties from a file and overrides them with system or environment variables
 * if available. The properties can be accessed using the static {@link #get(String)} method.
 * <p>
 * The properties file is expected to be in the classpath as "/application.properties".
 * Properties that can be loaded include database connection details, email service settings,
 * weather API keys, and Redis configuration.
 * <p>
 * Throws a {@link PropertiesLoadingException} if the properties file is not found, can't be loaded,
 * or if a required property is not set.
 */
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
