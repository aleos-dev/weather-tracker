package com.aleos.security.web.context;

import java.util.function.Supplier;

public final class SecurityContextHolder {

    private static final ThreadLocal<Supplier<SecurityContext>> contextHolder = new ThreadLocal<>();

    private SecurityContextHolder() {
        throw new IllegalStateException("Utility class");
    }

    public static SecurityContext getContext() {
        Supplier<SecurityContext> contextSupplier = contextHolder.get();

        if (contextSupplier == null) {
            SecurityContext context = createEmptyContext();
            contextSupplier = () -> context;
            contextHolder.set(contextSupplier);
        }

        return contextSupplier.get();
    }

    public static void setDeferredContext(Supplier<SecurityContext> contextSupplier) {
        contextHolder.set(contextSupplier);
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}
