package com.aleos.servlet;

import com.aleos.service.UserService;
import com.aleos.service.WeatherApiClient;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/locations")
public class LocationServlet extends AbstractThymeleafServlet {

    private transient WeatherApiClient weatherClient;
    private transient UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        weatherClient = serviceLocator.getBean(WeatherApiClient.class);
        userService = serviceLocator.getBean(UserService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        var locationsByQuery = weatherClient.searchLocationByName(req.getParameter("query"));
        req.setAttribute("locationsByQuery", locationsByQuery);

        processTemplate("weather", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        String username = retrieveAuthenticationPrincipal(req);

        var lon = Double.parseDouble(req.getParameter("lon"));
        var lat = Double.parseDouble(req.getParameter("lat"));
        var locationName = req.getParameter("locationName");

        userService.setLocationForUser(username, locationName, lon, lat);

        sendRedirect("/api/v1/weather", res);
    }
}
