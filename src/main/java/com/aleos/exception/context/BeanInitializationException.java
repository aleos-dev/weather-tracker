package com.aleos.exception.context;

/**
 * Exception thrown in case of an error during the initialization of a bean.
 * This exception is typically used to signal a problem with creating or
 * configuring beans within a dependence-injection framework.
 * <p>
 * This class extends {@link RuntimeException}, allowing it to be thrown during
 * runtime without the need for explicit handling.
 * <p>
 * Constructors:
 * - {@code BeanInitializationException(String message)}: Creates a new instance
 * with the provided error message.
 * - {@code BeanInitializationException(String message, Exception cause)}: Creates
 * a new instance with the provided error message and cause.
 */
public class BeanInitializationException extends RuntimeException {

    public BeanInitializationException(String message) {
        super(message);
    }

    public BeanInitializationException(String message, Exception cause) {
        super(message, cause);
    }
}
