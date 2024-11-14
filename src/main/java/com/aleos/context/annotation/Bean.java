package com.aleos.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Bean annotation is used to indicate that a method instantiates, configures, and initializes a new object to be managed by the application context.
 * <p>
 * It can be applied to methods and parameters.
 * <p>
 * The name() element allows assigning a unique identifier to the bean, otherwise, the bean will default to the method name.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
    String name() default "";
}
