package com.aleos.context.servicelocator;

import com.aleos.context.annotation.Bean;
import com.aleos.exception.security.BeanInitializationException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory implements ServiceLocator {

    public static final String BEAN_FACTORY_CONTEXT_KEY = "BEAN_FACTORY_CONTEXT_KEY";

    private final Map<String, Object> beans = new ConcurrentHashMap<>();

    public BeanFactory(Class<?> configClass) {
        initializeBeans(configClass);
    }

    @Override
    public <T> T getBean(Class<T> contextKey) {
        return (T) beans.get(contextKey.getSimpleName());
    }

    @Override
    public void registerBean(Class<?> contextKey, Object obj) {
        registerBean(contextKey.getSimpleName(), obj);
    }


    private void registerBean(String contextKey, Object obj) {
        if (beans.containsKey(contextKey)) {
            throw new BeanInitializationException(
                    String.format("The bean under contextKey: %s is already registered.", contextKey));
        }

        beans.put(contextKey, obj);
    }

    private void initializeBeans(Class<?> configClass) {
        try {
            Object configInstance = configClass.getDeclaredConstructor().newInstance();

            Deque<Method> methods = new ArrayDeque<>(Arrays.stream(configClass.getDeclaredMethods()).toList());

            int retriesCount = 0;

            while (!methods.isEmpty()) {
                var method = methods.poll();
                var beanAnnotation = method.getAnnotation(Bean.class);
                if (beanAnnotation == null) {
                    continue;
                }
                String contextKey =  determineBeanContextKey(beanAnnotation, method);

                var params = resolveMethodParameters(method);

                if (params.isEmpty()) {
                    if (isCycle(++retriesCount, methods.size())) {
                        throw new BeanInitializationException("Failed to initialize the next bean: " + contextKey);
                    }
                    methods.add(method);
                } else {
                    retriesCount = 0;
                    Object bean = method.invoke(configInstance, params.get());
                    registerBean(contextKey, bean);
                }
            }
        } catch (Exception e) {
            throw new BeanInitializationException("Failed to initialize beans", e);
        }
    }

    private String determineBeanContextKey(Bean beanAnnotation, Method method) {
        return beanAnnotation.name().isEmpty()
                ? Character.toUpperCase(method.getName().charAt(0)) + method.getName().substring(1)
                : Character.toUpperCase(beanAnnotation.name().charAt(0)) + beanAnnotation.name().substring(1);
    }

    private static boolean isCycle(int retriesCount, int methodsCount) {
        return retriesCount > methodsCount;
    }

    private Optional<Object[]> resolveMethodParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String beanContextKey = parameters[i].getType().getSimpleName();

            var bean = beans.get(beanContextKey);
            if (bean == null) {
                return Optional.empty();
            }
            params[i] = bean;
        }
        return Optional.of(params);
    }
}
