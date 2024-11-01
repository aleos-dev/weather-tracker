package com.aleos.util;

import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.*;

public final class ReflectionUtil {

    private static final Logger logger = getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void setFieldToObject(Object obj, String fieldName, Object value) {
        Stream.concat(
                        Arrays.stream(obj.getClass().getDeclaredFields()),
                        Arrays.stream(obj.getClass().getSuperclass().getDeclaredFields()))
                .filter(field -> field.getName().equals(fieldName))
                .findAny()
                .ifPresent(field -> setValue(obj, field, value));
    }

    private static void setValue(Object obj, Field field, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error("Error setting field: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
