package com.aleos.servlet;

import com.aleos.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/weather")
public class WeatherServlet extends AbstractThymeleafServlet {

    private transient UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = serviceLocator.getBean(UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        var username = retrieveAuthenticationPrincipal(req);
        var userLocationsWeather = userService.findWeatherForAllLocationsByUsername(username);

        req.setAttribute("userLocationsWeather", userLocationsWeather);

        processTemplate("weather", req, res);
    }
}
