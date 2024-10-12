package com.aleos.security.web.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CharacterEncodingFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String path = req.getRequestURI();
        if (path.startsWith("/api")) {
            req.setCharacterEncoding(UTF_8.name());
            res.setCharacterEncoding(UTF_8.name());
            res.setContentType("text/html; charset=UTF-8");
        }

        chain.doFilter(req, res);
    }
}
