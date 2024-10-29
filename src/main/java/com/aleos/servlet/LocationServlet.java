package com.aleos.servlet;

import com.aleos.service.UserService;
import com.aleos.service.WeatherApiClient;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (Objects.equals(req.getParameter("_method"), "DELETE")) {
            doDelete(req, resp);
            return;
        }
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        getLocationQuery(req)
                .map(weatherClient::searchLocationByName)
                .ifPresent(locations -> req.setAttribute("locationsByQuery", locations));

        processTemplate("weather", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        String username = retrieveAuthenticationPrincipal(req);

        var lon = parseCoordinate("lon", req);
        var lat = parseCoordinate("lat", req);
        var locationName = req.getParameter("locationName");

        userService.setLocationForUser(
                username,
                locationName == null ? lon + "," + lat : locationName.trim(),
                lon,
                lat);

        sendRedirect("/api/v1/weather", res);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) {
        String username = retrieveAuthenticationPrincipal(req);

        var lon = parseCoordinate("lon", req);
        var lat = parseCoordinate("lat", req);

        userService.removeLocationForUser(username, lon, lat);

        sendRedirect("/api/v1/weather", res);
    }


    private double parseCoordinate(String key, HttpServletRequest req) {
        try {
            return Double.parseDouble(req.getParameter(key));
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalStateException("Invalid %s parameter: %s.".formatted(key, req.getParameter(key)));
        }
    }

    private Optional<String> getLocationQuery(HttpServletRequest req) {
        String query = req.getParameter("query");

        return (query == null || query.isBlank())
                ? Optional.empty()
                : Optional.of(query.trim());
    }
}
