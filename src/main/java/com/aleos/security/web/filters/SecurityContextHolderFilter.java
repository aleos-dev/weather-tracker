package com.aleos.security.web.filters;

import com.aleos.security.web.context.SecurityContext;
import com.aleos.security.web.context.SecurityContextHolder;
import com.aleos.security.web.context.SecurityContextRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.function.Supplier;

@AllArgsConstructor
public class SecurityContextHolderFilter extends HttpFilter {

    private final transient SecurityContextRepository securityContextRepository;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        Supplier<SecurityContext> contextSupplier = securityContextRepository.loadDeferredContext(req);
        SecurityContextHolder.setDeferredContext(contextSupplier);
        chain.doFilter(req, res);
    }
}
