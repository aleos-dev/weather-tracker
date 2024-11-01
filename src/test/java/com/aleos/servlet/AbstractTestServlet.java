package com.aleos.servlet;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.http.CustomHttpSession;
import com.aleos.http.CustomHttpSessionImpl;
import com.aleos.model.UserPayload;
import com.aleos.service.UserService;
import com.aleos.util.DbUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mock;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import static org.mockito.Mockito.when;

public class AbstractTestServlet {

    protected static final UserService userService =
            TestContextInitializer.getServiceLocator().getBean(UserService.class);

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

    protected void setupNoVerifiedUser(String username, String password, String email) {
        DbUtil.createNewUser(new UserPayload(username, password, email));
    }
}
