package com.aleos.servlet;

import com.aleos.context.listener.TestContextInitializer;
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

    private static WelcomeServlet spyServlet;

    @BeforeAll
    static void beforeAll() {
        WelcomeServlet realServlet = new WelcomeServlet();
        ReflectionUtil.setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        spyServlet = Mockito.spy(realServlet);
    }

    @BeforeEach
    void setUp() {
        ReflectionUtil.setFieldToObject(spyServlet, "templateEngine", templateEngine);
        doReturn(webContext).when(spyServlet).buildWebContext(request, response);
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
}