package com.aleos.http;

import com.aleos.util.Properties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final String SESSION_COOKIE_NAME = "SESSION_ID";
    private static final long SESSION_TIMEOUT_MS;
    private static final String APP_CONTEXT;

    static {
        SESSION_TIMEOUT_MS = Long.parseLong(Properties.get("session.timeout").orElse("3600000L"));
        APP_CONTEXT = Properties.get("app.context").orElse("/");
    }

    private final Map<UUID, CustomHttpSession> sessions = new ConcurrentHashMap<>();

    public CustomHttpSession createSession(HttpServletResponse res) {
        UUID sessionId = generateSessionId();
        CustomHttpSession session = new CustomHttpSessionImpl(sessionId);
        sessions.put(sessionId, session);

        createSessionIdCookie(res, sessionId);

        return session;
    }

    public Optional<CustomHttpSession> getValidSession(HttpServletRequest req, HttpServletResponse res) {
        return getSessionIdFromCookie(req).flatMap(sessionId -> {
                    CustomHttpSession session = sessions.get(sessionId);

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

    public void removeSession(UUID sessionId) {
        sessions.remove(sessionId);
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
}
