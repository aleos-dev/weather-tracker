package com.aleos.http;

import com.aleos.context.Properties;
import com.aleos.security.core.AuthenticationToken;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import static com.aleos.http.CustomHttpSessionImpl.AUTH_SESSION_KEY;
import static org.slf4j.LoggerFactory.*;

/**
 * Manages HTTP sessions by creating, validating, saving, and removing sessions.
 * Utilizes Redis for session storage and handles session cookies.
 */
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SessionManager {

    private static final Logger logger = getLogger(SessionManager.class);

    private static final String SESSION_COOKIE_NAME = "SESSION_ID";
    private static final long SESSION_TIMEOUT_MS;
    private static final String APP_CONTEXT;

    static {
        SESSION_TIMEOUT_MS = Long.parseLong(Properties.get("session.timeout").orElse("3600000L"));
        APP_CONTEXT = Properties.get("app.context").orElse("/");
    }

    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new HTTP session, generates a unique session ID, creates a session ID cookie,
     * and returns the newly created session.
     *
     * @param res the HttpServletResponse object used to add the session ID cookie.
     * @return a new instance of CustomHttpSession with a unique session ID.
     */
    public CustomHttpSession createSession(HttpServletResponse res) {
        UUID sessionId = generateSessionId();
        CustomHttpSession session = new CustomHttpSessionImpl(sessionId);

        createSessionIdCookie(res, sessionId);

        return session;
    }

    /**
     * Retrieves a valid HTTP session based on the session ID extracted from the request's cookies.
     * If the session does not exist, the session cookie is invalidated.
     * If the session exists but is not valid, the session is removed and the session cookie is invalidated.
     * If the session is valid, its last accessed time is updated.
     *
     * @param req the HttpServletRequest containing the client's request data
     * @param res the HttpServletResponse for sending the response to the client
     * @return an Optional containing the valid CustomHttpSession if found, or an empty Optional otherwise
     */
    public Optional<CustomHttpSession> getValidSession(HttpServletRequest req, HttpServletResponse res) {
        return getSessionIdFromCookie(req).flatMap(sessionId -> {

            CustomHttpSession session = getSession(sessionId);

            if (session == null) {
                invalidateSessionCookie(res);

            } else {

                if (isSessionValid(session)) {
                    updateLastAccessedTime(session);

                } else {
                    removeSession(sessionId);
                    invalidateSessionCookie(res);
                }
            }

            return Optional.ofNullable(session);
        });
    }

    /**
     * Saves the provided custom HTTP session to Redis storage.
     *
     * @param session the custom HTTP session to be saved; must not be null
     */
    public void saveSessionToRedis(CustomHttpSession session) {
        try (Jedis jedis = jedisPool.getResource()) {
            serializeSession(session).ifPresent(sessionData ->
                    jedis.setex(session.getId().toString(), SESSION_TIMEOUT_MS / 1000, sessionData));
        }
    }

    /**
     * Retrieves a session by its ID from Redis.
     *
     * @param sessionId the unique identifier of the session
     * @return the CustomHttpSession object if found, otherwise null
     */
    public CustomHttpSession getSession(UUID sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String sessionData = jedis.get(sessionId.toString());

            return sessionData == null
                    ? null
                    : deserializeSession(sessionData);
        }
    }

    /**
     * Removes the session associated with the given session ID from the Redis store.
     *
     * @param sessionId the unique identifier of the session to be removed
     */
    public void removeSession(UUID sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(sessionId.toString());
        }
    }

    private void createSessionIdCookie(HttpServletResponse res, UUID sessionId) {
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId.toString());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge((int) (SESSION_TIMEOUT_MS / 1000));
        sessionCookie.setPath(APP_CONTEXT);

        res.addCookie(sessionCookie);
    }

    private void invalidateSessionCookie(HttpServletResponse res) {
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, "");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath(APP_CONTEXT);

        res.addCookie(sessionCookie);
    }

    private Optional<UUID> getSessionIdFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (var cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    return parseUuidFromCookie(cookie);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> parseUuidFromCookie(Cookie cookie) {
        try {
            return Optional.of(UUID.fromString(cookie.getValue()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private UUID generateSessionId() {
        return UUID.randomUUID();
    }

    private boolean isSessionValid(CustomHttpSession session) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - session.getLastAccessedTime()) < SESSION_TIMEOUT_MS;
    }

    private void updateLastAccessedTime(CustomHttpSession session) {
        session.setLastAccessedTime(System.currentTimeMillis());
    }

    private Optional<String> serializeSession(CustomHttpSession session) {
        try {
            return Optional.of(objectMapper.writeValueAsString(session));
        } catch (JsonProcessingException e) {
            logger.error("Session serialization error: ", e);
            return Optional.empty();
        }
    }

    private CustomHttpSession deserializeSession(String sessionData) {
        try {
            var session = objectMapper.readValue(sessionData, CustomHttpSessionImpl.class);

            Optional.ofNullable(session.getAttribute(AUTH_SESSION_KEY))
                    .filter(LinkedHashMap.class::isInstance)
                    .map(auth -> objectMapper.convertValue(auth, AuthenticationToken.class))
                    .ifPresent(session::setAuthentication);

            return session;
        } catch (JsonProcessingException e) {
            logger.error("Session deserialization error: ", e);
            return null;
        }
    }
}
