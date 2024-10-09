package com.aleos.servlet;

import com.aleos.http.CustomHttpSession;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/v1/login")
public class LoginServlet extends AbstractThymeleafServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("login", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        var originalRequest = ((CustomHttpSession) req
                .getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY)).getOriginalRequest();

        var redirectUri = originalRequest == null ? "/" : originalRequest;

        sendRedirect(redirectUri, res);
    }
}
