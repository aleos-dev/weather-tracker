package com.aleos.security.web.context;

import com.aleos.http.CustomHttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionSecurityContextRepository implements SecurityContextRepository {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionSecurityContextRepository.class);

    public static final String SECURITY_CONTEXT_KEY = "SECURITY_CONTEXT";

    private static final String SESSION_CONTEXT_KEY = CustomHttpSession.SESSION_CONTEXT_KEY;


    @Override
    public SecurityContext loadContext(HttpServletRequest req) {
        logger.debug("Starting to load security context from request.");

        CustomHttpSession customHttpSession = readCustomHttpSession(req);

        SecurityContext context = readSecurityContextFromSession(customHttpSession);
        if (context == null) {
            logger.debug("No existing security context found, creating a new one.");
            context = createNewContext();
            saveContext(context, req);
        }

        logger.debug("Finished loading security context from request.");
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest req) {
        logger.debug("Starting to save security context to request.");

        CustomHttpSession session = readCustomHttpSession(req);
        if (session != null) {
            session.setAttribute(SECURITY_CONTEXT_KEY, context);
            logger.debug("Security context saved in session.");
        } else {
            logger.debug("No session found, cannot save security context.");
        }

        logger.debug("Finished saving security context to request.");
    }

    private CustomHttpSession readCustomHttpSession(HttpServletRequest req) {
        logger.debug("Reading CustomHttpSession from request.");
        return (CustomHttpSession) req.getAttribute(SESSION_CONTEXT_KEY);
    }

    private SecurityContext readSecurityContextFromSession(CustomHttpSession customHttpSession) {
        if (customHttpSession == null) {
            logger.debug("Cannot read security context from session. No session found.");
            return null;
        }

        Object contextFromSession = customHttpSession.getAttribute(SECURITY_CONTEXT_KEY);
        if (contextFromSession == null) {
            logger.debug("SecurityContext not found in session.");
            return null;
        }

        logger.debug("SecurityContext found in session.");
        return (SecurityContext) contextFromSession;
    }

    private SecurityContext createNewContext() {
        logger.debug("Creating a new empty SecurityContext.");
        return SecurityContextHolder.createEmptyContext();
    }
}
