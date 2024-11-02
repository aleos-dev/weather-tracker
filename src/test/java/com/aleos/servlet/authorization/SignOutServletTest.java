package com.aleos.servlet.authorization;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.servlet.AbstractTestServlet;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static com.aleos.util.ReflectionUtil.setFieldToObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SignOutServletTest extends AbstractTestServlet {

    private static SignOutServlet realServlet;
    private static SignOutServlet spyServlet;

    @BeforeAll
    static void beforeAll() {
        initializeDependencies();
        TestContextInitializer.getBean(Flyway.class).migrate();
    }

    @AfterAll
    static void afterAll() {
        TestContextInitializer.getBean(Flyway.class).clean();
    }

    @BeforeEach
    void setUp() {
        spyServlet = Mockito.spy(realServlet);
        configureSpyServlet(spyServlet);
    }

    @Test
    void doGet_ShouldInvalidateSession_WhenUserIsAuthenticated() {
        setupMockSessionForAuthenticatedUser();

        spyServlet.doGet(request, response);

        verify(session).invalidate();
    }

    @Test
    void doGet_ShouldRedirectToAuthenticationUri_WhenUserIsNotAuthenticated() throws IOException {
        setupMockSessionForUnauthenticatedUser();

        spyServlet.doGet(request, response);

        verify(response).sendRedirect(anyString());
    }

    private static void initializeDependencies() {
        realServlet = new SignOutServlet();
        setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
    }
}