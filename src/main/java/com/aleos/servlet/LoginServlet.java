package com.aleos.servlet;

import com.aleos.context.Properties;
import com.aleos.http.CustomHttpSession;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/login")
public class LoginServlet extends AbstractThymeleafServlet {

    private static final String DEFAULT_REDIRECT_URI = Properties.get("base.auth.url").orElse("/api/v1/weather");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        renderLoginPage(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute("errorData") == null) {

            var originalRequest = ((CustomHttpSession) req
                    .getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY)).getOriginalRequest();

            var redirectUri = originalRequest == null
                    ? DEFAULT_REDIRECT_URI : originalRequest;

            sendRedirect(redirectUri, res);

        } else {

            renderLoginPage(req, res);
        }
    }

    private void renderLoginPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("login", req, res);
    }
}
