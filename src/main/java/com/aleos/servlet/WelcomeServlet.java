package com.aleos.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet implementation class WelcomeServlet
 * <p>
 * This servlet handles HTTP GET requests to the "/api/v1/welcome" endpoint.
 * Depending on the authentication status of the user, it either redirects to the
 * weather page or displays the welcome page.
 * <p>
 * Inherits from AbstractThymeleafServlet to utilize Thymeleaf template processing:
 * - Redirects authenticated users to the weather page.
 * - Displays the welcome page for unauthenticated users.
 * <p>
 * Logging is used to trace the actions taken during the request handling.
 */
@Slf4j
@WebServlet("/api/v1/welcome")
public class WelcomeServlet extends AbstractThymeleafServlet {

    /**
     * Handles HTTP GET requests to either display the welcome page or redirect to the weather page
     * based on the authentication status of the user.
     *
     * @param req the HttpServletRequest object containing the client's request
     * @param res the HttpServletResponse object containing the servlet's response
     */
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
