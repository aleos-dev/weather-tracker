package com.aleos.servlet;

import com.aleos.http.CustomHttpSession;
import com.aleos.http.CustomHttpSessionImpl;
import com.aleos.model.UserPayload;
import com.aleos.model.entity.UserVerificationToken;
import com.aleos.util.DbUtil;
import com.aleos.util.ReflectionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AbstractTestServlet {

    @Mock
    protected static ITemplateEngine templateEngine;

    @Mock
    protected static WebContext webContext;

    @Mock
    protected CustomHttpSessionImpl session;

    @Mock
    protected HttpServletRequest request;

    @Mock
    protected HttpServletResponse response;

    protected void setupMockSessionForUnauthenticatedUser() {
        when(request.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY)).thenReturn(session);
        when(session.isAuthenticated()).thenReturn(false);
    }

    protected void setupMockSessionForAuthenticatedUser() {
        when(request.getAttribute(CustomHttpSession.SESSION_CONTEXT_KEY)).thenReturn(session);
        when(session.isAuthenticated()).thenReturn(true);
    }

    protected void setupVerifiedUser(String username, String password, String email) {
        var verificationToken = DbUtil.createNewUser(new UserPayload(username, password, email));
        DbUtil.verifyUserByToken(verificationToken.getToken());
    }

    protected UserVerificationToken setupNoVerifiedUser(String username, String password, String email) {
        return DbUtil.createNewUser(new UserPayload(username, password, email));
    }

    protected void configureSpyServlet(AbstractThymeleafServlet spyServlet) {
        ReflectionUtil.setFieldToObject(spyServlet, "templateEngine", templateEngine);
        lenient().doReturn(webContext).when(spyServlet).buildWebContext(request, response);
    }

    protected void verifyTemplateRendered(String templateName, AbstractThymeleafServlet spyServlet) {
        verify(spyServlet).processTemplate(eq(templateName), eq(request), eq(response));
    }

    protected void assertNonEmptyList(List<?> list, String message) {
        assertNotNull(list, "Expected a non-null list.");
        assertFalse(list.isEmpty(), message);
    }

    protected void assertEmptyList(List<?> list, String message) {
        assertNotNull(list, "Expected a non-null list.");
        assertTrue(list.isEmpty(), message);
    }
}
