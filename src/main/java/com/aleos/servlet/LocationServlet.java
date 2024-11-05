package com.aleos.servlet;

import com.aleos.exception.servlet.CoordinateParsingException;
import com.aleos.service.UserService;
import com.aleos.service.WeatherApiClient;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
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

        log.info("LocationServlet initialized with WeatherApiClient and UserService");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var method = req.getParameter(("_method"));
        if (method != null) {
            log.debug("Received request with method override parameter: _method={}", method);
        }

        if ("DELETE".equalsIgnoreCase(method)) {
            doDelete(req, resp);
        } else if ("PATCH".equalsIgnoreCase(method)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        log.info("Handling GET request for locations");

        getLocationQuery(req)
                .map(weatherClient::searchLocationByName)
                .ifPresent(locations -> req.setAttribute("locationsByQuery", locations));

        processTemplate("weather", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) {
        String username = retrieveAuthenticationPrincipal(req);
        log.info("Handling POST request to add location for user: {}", username);

        var lon = parseCoordinate("lon", req);
        var lat = parseCoordinate("lat", req);
        var locationName = req.getParameter("locationName");
        log.debug("Parsed coordinates: lon={}, lat={}, locationName={}", lon, lat, locationName);

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
        log.info("Handling DELETE request to remove location for user: {}", username);

        var lon = parseCoordinate("lon", req);
        var lat = parseCoordinate("lat", req);
        log.debug("Parsed coordinates for deletion: lon={}, lat={}", lon, lat);

        userService.removeLocationForUser(username, lon, lat);

        sendRedirect("/api/v1/weather", res);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse res) {
        String username = retrieveAuthenticationPrincipal(req);
        log.info("Handling PATCH request to rename location for user: {}", username);

        var lon = parseCoordinate("lon", req);
        var lat = parseCoordinate("lat", req);
        var newLocationName = req.getParameter("locationName");
        log.debug("Parsed coordinates for renaming: lon={}, lat={}, newLocationName={}", lon, lat, newLocationName);

        userService.renameLocationByCoordinates(username, lon, lat, newLocationName);

        res.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private double parseCoordinate(String key, HttpServletRequest req) {
        try {
            return Double.parseDouble(req.getParameter(key));
        } catch (NumberFormatException | NullPointerException e) {
            throw new CoordinateParsingException(
                    "Invalid %s coordinate parameter: %s.".formatted(key, req.getParameter(key)), e);
        }
    }

    private Optional<String> getLocationQuery(HttpServletRequest req) {
        String query = req.getParameter("query");
        log.debug("Retrieved location query parameter: {}", query);

        return (query == null || query.isBlank())
                ? Optional.empty()
                : Optional.of(query.trim());
    }
}
