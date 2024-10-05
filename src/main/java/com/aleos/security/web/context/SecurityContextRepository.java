package com.aleos.security.web.context;

import com.aleos.security.util.SingletonSupplier;
import jakarta.servlet.http.HttpServletRequest;

import java.util.function.Supplier;

public interface SecurityContextRepository {

    SecurityContext loadContext(HttpServletRequest req);

    default Supplier<SecurityContext> loadDeferredContext(HttpServletRequest req) {
        Supplier<SecurityContext> supplier = () -> loadContext(req);
        return SingletonSupplier.of(supplier);
    }

    void saveContext(SecurityContext context, HttpServletRequest req);
}
