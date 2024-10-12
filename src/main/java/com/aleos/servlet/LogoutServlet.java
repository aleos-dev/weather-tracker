package com.aleos.servlet;

import com.aleos.http.CustomHttpSession;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/logout")
public class LogoutServlet extends AbstractThymeleafServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        if (req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY) instanceof CustomHttpSession  session) {
            session.invalidate();
        }

        processTemplate("welcome", req, res);
    }
}
