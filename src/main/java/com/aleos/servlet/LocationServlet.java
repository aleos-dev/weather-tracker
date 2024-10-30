package com.aleos.servlet;

import com.aleos.service.UserService;
import com.aleos.service.WeatherApiClient;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@MultipartConfig
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
        switch (req.getParameter("_method").toUpperCase()) {
            case "DELETE" -> doDelete(req, resp);
            case "PATCH" -> doPatch(req, resp);
            default -> super.service(req, resp);
        }
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
        userService.removeLocationForUser(
                retrieveAuthenticationPrincipal(req),
                parseCoordinate("lon", req),
                parseCoordinate("lat", req)
        );

        sendRedirect("/api/v1/weather", res);
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse res) {
        userService.renameLocationByCoordinates(
                retrieveAuthenticationPrincipal(req),
                parseCoordinate("lon", req),
                parseCoordinate("lat", req),
                req.getParameter("locationName")
        );

        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
