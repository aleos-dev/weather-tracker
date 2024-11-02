package com.aleos.servlet;

import com.aleos.context.listener.TestContextInitializer;
import com.aleos.model.dto.LocationWeatherResponse;
import com.aleos.service.UserService;
import com.aleos.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.aleos.util.DbUtil.setLocationForUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServletTest extends AbstractTestServlet {

    private static WeatherServlet realServlet;
    private static WeatherServlet spyServlet;

    private static final String USERNAME = "WeatherServletTestUser";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "testWeatherServletEmail@localhost.com";
    private static final String LOCATION_NAME = "Kyiv - UA";
    private static final double LONGITUDE = 50.4500336;
    private static final double LATITUDE = 30.5241361;

    @BeforeAll
    static void setupOnce() {
        initializeDependencies();
    }

    @BeforeEach
    void setupPerTest() {
        spyServlet = Mockito.spy(realServlet);
        configureSpyServlet(spyServlet);
    }

    @Test
    void doGet_ShouldRenderTemplateAndRetrieveWeatherData() {
        setupVerifiedUser(USERNAME, PASSWORD, EMAIL);
        setLocationForUser(USERNAME, LOCATION_NAME, LONGITUDE, LATITUDE);
        doReturn(USERNAME).when(spyServlet).retrieveAuthenticationPrincipal(request);

        spyServlet.doGet(request, response);

        verify(spyServlet).retrieveAuthenticationPrincipal(request);
        List<LocationWeatherResponse> capturedData = captureWeatherDataFromRequest();
        assertNonEmptyList(capturedData, "Expected weather response to be found based on the query.");
        assertWeatherData(capturedData.getFirst());
        verifyTemplateRendered("weather", spyServlet);
    }

    @Test
    void doGet_ShouldReturnEmptyWeatherDataList_WhenUserHasNoSavedLocations() {
        var username = "testEmptyList";
        setupVerifiedUser(username, PASSWORD, "testEmptyList@localhost.com");
        doReturn(username).when(spyServlet).retrieveAuthenticationPrincipal(request);

        spyServlet.doGet(request, response);

        verify(spyServlet).retrieveAuthenticationPrincipal(request);
        assertEmptyList(captureWeatherDataFromRequest(), "Expected weather response to be empty.");
        verifyTemplateRendered("weather", spyServlet);
    }

    private static void initializeDependencies() {
        realServlet = new WeatherServlet();
        ReflectionUtil.setFieldToObject(realServlet, "serviceLocator", TestContextInitializer.getServiceLocator());
        ReflectionUtil.setFieldToObject(realServlet, "userService", TestContextInitializer.getBean(UserService.class));
    }


    private List<LocationWeatherResponse> captureWeatherDataFromRequest() {
        ArgumentCaptor<List<LocationWeatherResponse>> captor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("userLocationsWeather"), captor.capture());

        return captor.getValue();
    }

    private void assertWeatherData(LocationWeatherResponse actualResponse) {
        assertEquals(LOCATION_NAME, actualResponse.getLocationName());
        assertEquals(LONGITUDE, actualResponse.getLongitude());
        assertEquals(LATITUDE, actualResponse.getLatitude());
        assertNotNull(actualResponse.getWeatherDetails(), "Weather details should not be null");
    }
}