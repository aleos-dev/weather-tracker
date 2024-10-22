package com.aleos.servlet.authorization;

import com.aleos.context.Properties;
import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/login")
public class LoginServlet extends AbstractThymeleafServlet {

    private static final String DEFAULT_REDIRECT_URI = Properties.get("base.auth.url").orElse("/api/v1/weather");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        if (getSessionContext(req).isAuthenticated()) {

            renderMainPage(req, res);

        } else {

            renderLoginPage(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute("errorData") == null) {

            var originalRequest = getSessionContext(req).getOriginalRequest();

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

    private void renderMainPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("weather", req, res);
    }
}
