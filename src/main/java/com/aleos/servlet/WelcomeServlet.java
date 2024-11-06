package com.aleos.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/api/v1/welcome")
public class WelcomeServlet extends AbstractThymeleafServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        log.info("Handling GET request for welcome page.");

        if (getSessionContext(req).isAuthenticated()) {
            log.info("User is authenticated; redirecting to weather page.");
            sendRedirect(WEATHER_URI, res);
            return;
        }

        log.info("User is not authenticated; displaying welcome page.");
        processTemplate("welcome", req, res);
    }
}
