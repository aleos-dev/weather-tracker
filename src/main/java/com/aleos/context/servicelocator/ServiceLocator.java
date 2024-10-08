package com.aleos.context.servicelocator;

public interface ServiceLocator {

    <T> T getBean(Class<T> contextKey);

    void registerBean(Class<?> contextKey, Object obj);
}
