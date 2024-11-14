package com.aleos.servlet.filter;

import com.aleos.context.servicelocator.BeanFactory;
import com.aleos.context.servicelocator.ServiceLocator;
import com.aleos.http.CustomHttpSession;
import com.aleos.http.SessionManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * The SessionFilter class is responsible for managing HTTP session operations.
 * It initializes the session manager and handles HTTP request filtering by managing session validity.
 * <p>
 * This filter ensures each incoming HTTP request has a valid session. If a valid session is not found,
 * a new session is created. The filter also saves the session back to the Redis store if applicable.
 */
@Slf4j
public class SessionFilter extends HttpFilter {

    private static final String APP_RESOURCE_PREFIX = "/api/v1/";

    private transient SessionManager manager;

    /**
     * Initializes the SessionFilter with the necessary SessionManager dependency.
     * This method retrieves the ServiceLocator from the servlet context and obtains
     * the SessionManager bean required for session management.
     *
     * @param config The FilterConfig object that contains the filter's configuration
     *               and initialization parameters.
     */
    @Override
    public void init(FilterConfig config) {
        log.info("Initializing SessionFilter with SessionManager dependency");
        var locator = (ServiceLocator) config.getServletContext().getAttribute(BeanFactory.BEAN_FACTORY_CONTEXT_KEY);
        manager = locator.getBean(SessionManager.class);
    }

    /**
     * Filters incoming requests to ensure that a valid session is present.
     * If a valid session is not found, it creates a new session.
     * Sessions pertaining to application resources are saved after request processing.
     *
     * @param req   The HttpServletRequest object that contains the request the client made to the servlet.
     * @param res   The HttpServletResponse object that contains the response the servlet sends to the client.
     * @param chain The FilterChain for invoking the next filter or servlet in the chain.
     * @throws ServletException If the request could not be handled.
     * @throws IOException      If an input or output error occurred while the filter was handling the request.
     */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        log.debug("Processing request: {} {}", req.getMethod(), req.getRequestURI());

        CustomHttpSession session = manager
                .getValidSession(req, res)
                .orElseGet(() -> createNewSession(res));

        req.setAttribute(CustomHttpSession.SESSION_CONTEXT_KEY, session);
        log.debug("Session set in request with ID: {}", session.getId());

        try {
            chain.doFilter(req, res);
        } finally {

            // Lazy check (easy dirty checking:)) to determine if session needs to be saved
            if (isAppResources(req)) {
                log.debug("Saving session to Redis with ID: {}", session.getId());
                manager.saveSessionToRedis(session);
            } else {
                log.debug("Skipping session save for non-application resource request.");
            }
        }
    }

    private CustomHttpSession createNewSession(HttpServletResponse res) {
        log.info("No valid session found; creating a new session.");
        return manager.createSession(res);
    }

    private boolean isAppResources(HttpServletRequest req) {
        return req.getRequestURI().startsWith(APP_RESOURCE_PREFIX);
    }
}
