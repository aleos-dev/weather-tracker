package com.aleos.servlet.authorization;

import com.aleos.http.CustomHttpSession;
import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AbstractAuthServlet extends AbstractThymeleafServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (hasNoErrorAttribute(req) && getSessionContext(req).isAuthenticated()) {
            var originalRequest = getSessionContext(req).getOriginalRequest();
            var redirectUri = originalRequest == null ? DEFAULT_WEATHER_URI : originalRequest;
            sendRedirect(redirectUri, res);
            return;
        }

        super.service(req, res);
    }

    protected void renderSignInPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("sign-in", req, res);
    }

    protected void renderSignUpPage(HttpServletRequest req, HttpServletResponse res) {
        processTemplate("sign-up", req, res);
    }

    protected void signOutSession(HttpServletRequest req) {
        if (req.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY) instanceof CustomHttpSession  session) {
            session.invalidate();
        }
    }

}
