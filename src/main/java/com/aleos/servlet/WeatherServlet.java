package com.aleos.servlet;

import com.aleos.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * WeatherServlet is a web servlet responsible for handling HTTP requests related to weather data.
 * It leverages Thymeleaf for template processing and utilizes UserService to fetch weather information.
 */
@Slf4j
@WebServlet("/api/v1/weather")
public class WeatherServlet extends AbstractThymeleafServlet {

    private transient UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = serviceLocator.getBean(UserService.class);

        log.info("WeatherServlet initialized with UserService");
    }

    /**
     * Handles the HTTP GET requests to retrieve weather data for the authenticated user.
     *
     * @param req the HttpServletRequest object containing the client's request
     * @param res the HttpServletResponse object containing the servlet's response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        var username = retrieveAuthenticationPrincipal(req);
        log.info("Handling GET request for weather data for user: {}", username);

        var userLocationsWeather = userService.findWeatherForAllLocationsByUsername(username);
        log.debug("Weather data retrieved for user {}: {}", username, userLocationsWeather);

        req.setAttribute("userLocationsWeather", userLocationsWeather);
        log.debug("Weather data set as request attribute for template processing");

        processTemplate("weather", req, res);
    }
}
