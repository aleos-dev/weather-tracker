package com.aleos.servlet.authorization;

import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AbstractAuthServlet extends AbstractThymeleafServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (hasNoErrorAttribute(req) && getSessionContext(req).isAuthenticated()) {
            sendRedirect(getRedirectUri(req), res);

        } else {
            super.service(req, res);
        }
    }

    private String getRedirectUri(HttpServletRequest req) {
        var originalRequestUri = getSessionContext(req).getOriginalRequest();

        return (originalRequestUri == null || isALoop(req, originalRequestUri))
                ? WEATHER_URI
                : originalRequestUri;
    }

    private boolean isALoop(HttpServletRequest req, String originalRequestUri) {
        String currentUri = req.getServletPath();
        return currentUri.equalsIgnoreCase(originalRequestUri);
    }

    protected void renderSignInPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("sign-in", req, res);
    }

    protected void renderSignUpPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("sign-up", req, res);
    }
}
