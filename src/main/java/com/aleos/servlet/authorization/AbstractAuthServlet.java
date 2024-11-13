package com.aleos.servlet.authorization;

import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * AbstractAuthServlet is an abstract base class for handling authentication-related
 * HTTP servlet requests. It extends AbstractThymeleafServlet to provide Thymeleaf-based
 * rendering capabilities.
 */
public class AbstractAuthServlet extends AbstractThymeleafServlet {

    /**
     * Processes the HTTP request and response based on the user's authentication status
     * and the presence of error attributes in the request.
     *
     * @param req the HttpServletRequest object that contains the client's request
     * @param res the HttpServletResponse object that contains the servlet's response
     * @throws ServletException if an input or output error is detected when the servlet handles the request
     * @throws IOException      if the request for the service could not be handled
     */
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
