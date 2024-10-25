package com.aleos.servlet.authorization;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/sign-out")
public class SignOutServlet extends AbstractAuthServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        if (getSessionContext(req).isAuthenticated()) {

            signOutSession(req);

            sendRedirect(DEFAULT_WELCOME_URI, res);
            processTemplate("welcome", req, res);

        } else {
            sendRedirect(DEFAULT_AUTH_URI, res);
        }
    }
}
