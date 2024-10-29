package com.aleos.servlet.authorization;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/sign-in")
public class SignInServlet extends AbstractAuthServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        renderSignInPage(request, response);
    }

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
