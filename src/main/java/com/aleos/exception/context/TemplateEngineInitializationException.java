package com.aleos.exception.context;

/**
 * Exception thrown when the Template Engine fails to initialize.
 * <p>
 * This exception is a RuntimeException that is specifically used to indicate
 * an error during the initialization of a Template Engine, commonly in web applications
 * where the ability to render templates is crucial.
 * <p>
 * Use this exception to communicate failures that involve setting up a Template Engine,
 * such as incorrect configuration settings or missing resources.
 */
public class TemplateEngineInitializationException extends RuntimeException {

    public TemplateEngineInitializationException(String message) {
        super(message);
    }
}
