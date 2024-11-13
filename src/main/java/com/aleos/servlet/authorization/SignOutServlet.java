package com.aleos.servlet.authorization;

import com.aleos.http.CustomHttpSession;
import com.aleos.servlet.AbstractThymeleafServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SignOutServlet handles the sign-out process for authenticated users.
 * <p>
 * This servlet is mapped to the "/api/v1/sign-out" URL and extends the AbstractThymeleafServlet class.
 * It invalidates the user's session if they are authenticated and redirects them to the welcome URI.
 * If the user is not authenticated, it redirects them to the authentication URI.
 */
@WebServlet("/api/v1/sign-out")
public class SignOutServlet extends AbstractThymeleafServlet {

    /**
     * Handles the sign-out process for the user. If the user is authenticated,
     * invalidates the session and redirects to the welcome URI. Otherwise, redirects
     * to the authentication URI.
     *
     * @param req the HttpServletRequest object that contains the request the client made to the servlet
     * @param res the HttpServletResponse object that contains the response the servlet returns to the client
     */
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
