package com.aleos.servlet.authorization;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SignInServlet handles HTTP GET and POST requests for the sign-in functionality.
 * It extends the AbstractAuthServlet to leverage common authentication-related behaviors.
 */
@WebServlet("/api/v1/sign-in")
public class SignInServlet extends AbstractAuthServlet {

    /**
     * Handles HTTP GET requests for the sign-in page by rendering the sign-in template.
     *
     * @param request  the HttpServletRequest object that contains the client's request
     * @param response the HttpServletResponse object that contains the servlet's response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        renderSignInPage(request, response);
    }

    /**
     * Handles HTTP POST requests for the sign-in process. Redirects authenticated users to the weather page.
     * If the user is not authenticated, it preserves the user's name and renders the sign-in page again.
     *
     * @param request the HttpServletRequest object that contains the client's request
     * @param response the HttpServletResponse object that contains the servlet's response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        if (getSessionContext(request).isAuthenticated()) {
            sendRedirect(WEATHER_URI, response);
        } else {
            handleUnauthenticatedUser(request, response);
        }
    }

    private void handleUnauthenticatedUser(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("name", request.getParameter("name"));
        renderSignInPage(request, response);
    }
}
