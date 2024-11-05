package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.WeatherClientException;
import com.aleos.model.WeatherApiResponse;
import com.aleos.model.entity.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
@RequiredArgsConstructor
public class OpenWeatherApiClient implements WeatherApiClient {

    private static final String WEATHER_API_KEY = Properties.get("WEATHER_API_KEY").orElseThrow();
    private static final String METRIC_SYSTEM = Properties.get("weather.api.units").orElse("metric");
    private static final int API_RESPONSE_LIMIT = Integer.parseInt(Properties.get("weather.api.response.limit").orElse("8"));

    private static final String NAME_FORMAT = "%s - %s";
    private static final String OPEN_WEATHER_URL_FORMAT = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=%s";
    private static final String GEOCODING_URL_FORMAT = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<Location> searchLocationByName(String locationName) {
        log.info("Searching location by name: {}", locationName);

        HttpRequest geocodingRequest = HttpRequest.newBuilder()
                .uri(createGeocodingUri(locationName))
                .GET()
                .build();

        HttpResponse<String> response = getStringHttpResponse(geocodingRequest);
        log.debug("Received geocoding response for location: {}", locationName);

        return parseLocations(response.body());
    }

    @Override
    public WeatherApiResponse getWeatherByLocation(double longitude, double latitude) {
        log.info("Fetching weather for coordinates: lon={}, lat={}", longitude, latitude);

        HttpRequest locationRequest = HttpRequest.newBuilder()
                .uri(createLocationUri(longitude, latitude))
                .GET()
                .build();

        HttpResponse<String> response = getStringHttpResponse(locationRequest);
        log.debug("Received weather response for coordinates: lon={}, lat={}", longitude, latitude);

        return response.statusCode() == SC_OK
                ? parseWeatherResponse(response.body())
                : new WeatherApiResponse();
    }

    private WeatherApiResponse parseWeatherResponse(String responseBody) {
        try {
            log.debug("Parsing weather data from response body");
            WeatherApiResponse weatherResponse = objectMapper.readValue(responseBody, WeatherApiResponse.class);
            weatherResponse.setDataAvailable(true);
            log.debug("Parsing weather data from response body");
            return weatherResponse;
        } catch (JsonProcessingException e) {
            throw new WeatherClientException("Failed to parse weather data", e);
        }
    }

    private URI createGeocodingUri(String locationName) {
        return URI.create(String.format(GEOCODING_URL_FORMAT, locationName, API_RESPONSE_LIMIT, WEATHER_API_KEY));
    }

    private URI createLocationUri(double longitude, double latitude) {
        return URI.create(String.format(OPEN_WEATHER_URL_FORMAT, longitude, latitude, WEATHER_API_KEY, METRIC_SYSTEM));
    }

    private List<Location> parseLocations(String body) {
        try {
            log.debug("Parsing locations from response body");
            JsonNode rootNode = objectMapper.readTree(body);

            List<Location> locations = new ArrayList<>();
            for (JsonNode node : rootNode) {

                String name = extractName(node);
                double lon = node.get("lon").asDouble();
                double lat = node.get("lat").asDouble();

                locations.add(buildLocation(name, lon, lat));
            }
            log.info("Parsed {} locations successfully", locations.size());
            return locations;

        } catch (JsonProcessingException e) {
            throw new WeatherClientException("Failed to parse location data", e);
        }
    }

    private HttpResponse<String> getStringHttpResponse(HttpRequest geocodingRequest) {
        try {
            log.info("Sending request to {}", geocodingRequest.uri());
            var response = httpClient.send(geocodingRequest, HttpResponse.BodyHandlers.ofString());
            log.debug("Received response with status code: {}", response.statusCode());

            return checkResponse(response);

        } catch (IOException e) {
            throw new WeatherClientException("Error occurred while calling the weather service", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted");
            throw new WeatherClientException("Thread was interrupted while calling the weather service", e);
        }
    }

    private HttpResponse<String> checkResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        log.debug("Checking response status code: {}", statusCode);

        return switch (statusCode) {
            case SC_OK -> response;
            case SC_NOT_FOUND -> {
                log.warn("Location not found: 404");
                throw new WeatherClientException("Location not found. 404");
            }
            default -> {
                log.error("Unexpected status code received: {}", statusCode);
                throw new WeatherClientException("Failed to fetch weather data: Status Code " + statusCode);
            }
        };
    }

    private Location buildLocation(String name, double lon, double lat) {
        return new Location(name, lon, lat);
    }

    private String extractName(JsonNode node) {
        return NAME_FORMAT.formatted(node.get("name").asText(), extractIdentifier(node));
    }

    private String extractIdentifier(JsonNode node) {
        String country = node.get("country").asText();
        JsonNode stateNode = node.get("state");
        return stateNode != null
                ? String.join(", ", country, stateNode.asText())
                : country;
    }
}
