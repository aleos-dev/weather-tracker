package com.aleos.servlet;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.service.UserService;
import com.aleos.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WelcomeServletTest extends AbstractTestServlet {

    private static WelcomeServlet realServlet;
    private static WelcomeServlet spyServlet;

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
    void doGet_ShouldRedirectToWeatherURL_WhenAuthorized() throws IOException {
        setupMockSessionForAuthenticatedUser();

        spyServlet.doGet(request, response);

        verify(response).sendRedirect(AbstractThymeleafServlet.WEATHER_URI);
    }

    @Test
    void doGet_ShouldRenderTemplate_WhenIsNoAuthorized() {
        setupMockSessionForUnauthenticatedUser();

        spyServlet.doGet(request, response);

        verify(spyServlet).processTemplate("welcome", request, response);
    }

    private static void initializeDependencies() {
        realServlet = new WelcomeServlet();
        ReflectionUtil.setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        ReflectionUtil.setFieldToObject(realServlet, "userService", TestContextInitializer.getBean(UserService.class));
    }
}