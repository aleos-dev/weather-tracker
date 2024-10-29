package com.aleos.security.web.context;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionSecurityContextRepository implements SecurityContextRepository {

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionSecurityContextRepository.class);

    @Override
    public SecurityContext loadContext(HttpServletRequest req) {
        logger.debug("Creating a new empty SecurityContext.");
        return SecurityContextHolder.createEmptyContext();
    }
}
