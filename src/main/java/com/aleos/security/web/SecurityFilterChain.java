package com.aleos.security.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface SecurityFilterChain {

    boolean matches(HttpServletRequest request);

    void apply(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException;
}
