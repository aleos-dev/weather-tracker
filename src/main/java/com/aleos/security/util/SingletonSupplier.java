package com.aleos.security.util;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class SingletonSupplier<T> implements Supplier<T> {

    private final Supplier<? extends T> instanceSupplier;

    private volatile T singletonInstance;

    private final Lock writeLock = new ReentrantLock();

    private SingletonSupplier(Supplier<? extends T> supplier) {
        this.instanceSupplier = supplier;
    }

    @Override
    public T get() {
        T instance = this.singletonInstance;
        if (instance == null) {
            this.writeLock.lock();
            try {
                instance = this.singletonInstance;
                if (instance == null) {
                    if (this.instanceSupplier != null) {
                        instance = this.instanceSupplier.get();
                    }

                    this.singletonInstance = instance;
                }
            }
            finally {
                this.writeLock.unlock();
            }
        }
        return instance;
    }

    public static <T> SingletonSupplier<T> of(Supplier<T> supplier) {
        return new SingletonSupplier<>(supplier);
    }
}
