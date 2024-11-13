package com.aleos.security.web.context;

import com.aleos.security.util.SingletonSupplier;
import jakarta.servlet.http.HttpServletRequest;

import java.util.function.Supplier;

/**
 * A repository responsible for loading and storing SecurityContext instances for a given HttpServletRequest.
 * <p>
 * The SecurityContext represents the security information, including authentication and authorization data,
 * associated with the current request.
 */
public interface SecurityContextRepository {

    SecurityContext loadContext(HttpServletRequest req);

    default Supplier<SecurityContext> loadDeferredContext(HttpServletRequest req) {
        Supplier<SecurityContext> supplier = () -> loadContext(req);
        return SingletonSupplier.of(supplier);
    }
}
