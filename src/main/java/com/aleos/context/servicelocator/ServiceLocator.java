package com.aleos.context.servicelocator;

/**
 * The ServiceLocator interface provides methods for retrieving and registering beans
 * in a context, which allows for dependency injection and management within an application.
 * <p>
 * Methods:
 * - {@code <T> T getBean(Class<T> contextKey)}: Retrieves an instance of the bean associated
 * with the given context key. The type of the bean is specified by the generic parameter {@code T}.
 * - {@code void registerBean(Class<?> contextKey, Object obj)}: Registers a new bean with the
 * specified context key. The context key is typically the class or interface that the bean
 * implements or extends, and {@code obj} is the instance of the bean to be registered.
 */
public interface ServiceLocator {

    <T> T getBean(Class<T> contextKey);

    void registerBean(Class<?> contextKey, Object obj);
}
