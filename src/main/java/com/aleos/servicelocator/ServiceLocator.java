package com.aleos.servicelocator;

public interface ServiceLocator {

    <T> T getBean(Class<T> contextKey);

    void registerBean(Class<?> contextKey, Object obj);
}
