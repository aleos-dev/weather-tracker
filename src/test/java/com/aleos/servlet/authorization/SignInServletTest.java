package com.aleos.servlet.authorization;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.servlet.AbstractTestServlet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static com.aleos.util.ReflectionUtil.setFieldToObject;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignInServletTest extends AbstractTestServlet {

    private static SignInServlet realServlet;
    private static SignInServlet spyServlet;

    @BeforeAll
    static void beforeAll() {
        initializeDependencies();
    }

    @BeforeEach
    void setUp() {
        spyServlet = Mockito.spy(realServlet);
        configureSpyServlet(spyServlet);
    }

    @Test
    void doGet_ShouldRenderSignInPage() {
        spyServlet.doGet(request, response);

        verifyTemplateRendered("sign-in", spyServlet);
    }

    @Test
    void doPost_ShouldRedirectOnWeatherPage_WhenUserAlreadyAuthenticated() throws IOException {
        setupMockSessionForAuthenticatedUser();

        spyServlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    void doPost_ShouldRenderSignInPageAndPreserveUserName_WhenUserIsNotAuthenticated() {
        var username = "notAuthenticatedUser";
        mockNameParameter(username);
        setupMockSessionForUnauthenticatedUser();

        spyServlet.doPost(request, response);

        verify(request).setAttribute("name", username);
        verifyTemplateRendered("sign-in", spyServlet);
    }

    private void mockNameParameter(String username) {
        doReturn(username).when(request).getParameter("name");
    }

    private static void initializeDependencies() {
        realServlet = new SignInServlet();
        setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
    }
}