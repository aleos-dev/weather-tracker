package com.aleos.servlet.authorization;

import com.aleos.http.CustomHttpSession;
import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/sign-out")
public class SignOutServlet extends AbstractThymeleafServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        if (getSessionContext(req).isAuthenticated()) {

            signOutSession(req);
            sendRedirect(WELCOME_URI, res);

        } else {
            sendRedirect(AUTH_URI, res);
        }
    }

    private void signOutSession(HttpServletRequest req) {
        if (req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY) instanceof CustomHttpSession  session) {
            session.invalidate();
        }
    }
}
