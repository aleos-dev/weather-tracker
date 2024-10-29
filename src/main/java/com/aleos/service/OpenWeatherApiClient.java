package com.aleos.service;

import com.aleos.context.Properties;
import com.aleos.exception.WeatherClientException;
import com.aleos.model.WeatherApiResponse;
import com.aleos.model.entity.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@RequiredArgsConstructor
public class OpenWeatherApiClient implements WeatherApiClient {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(OpenWeatherApiClient.class);

    private static final String API_KEY = validateApiKey();
    private static final String METRIC_SYSTEM = Properties.get("weather.api.units").orElse("metric");
    private static final int API_RESPONSE_LIMIT = Integer.parseInt(Properties.get("weather.api.response.limit").orElse("8"));

    private static final String NAME_FORMAT = "%s - %s";
    private static final String OPEN_WEATHER_URL_FORMAT = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=%s";
    private static final String GEOCODING_URL_FORMAT = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<Location> searchLocationByName(String locationName) {
        HttpRequest geocodingRequest = HttpRequest.newBuilder()
                .uri(createGeocodingUri(locationName))
                .GET()
                .build();

        HttpResponse<String> response = getStringHttpResponse(geocodingRequest);

        return parseLocations(response.body());
    }

    @Override
    public WeatherApiResponse getWeatherByLocation(double longitude, double latitude) {
        HttpRequest locationRequest = HttpRequest.newBuilder()
                .uri(createLocationUri(longitude, latitude))
                .GET()
                .build();

        HttpResponse<String> response = getStringHttpResponse(locationRequest);

        return response.statusCode() == SC_OK
                ? parseWeatherResponse(response.body())
                : new WeatherApiResponse();
    }

    private WeatherApiResponse parseWeatherResponse(String responseBody) {
        try {
            WeatherApiResponse weatherResponse = objectMapper.readValue(responseBody, WeatherApiResponse.class);
            weatherResponse.setDataAvailable(true);
            return weatherResponse;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse weather data", e);
            throw new WeatherClientException("Failed to parse weather data");
        }
    }

    private URI createGeocodingUri(String locationName) {
        return URI.create(
                String.format(GEOCODING_URL_FORMAT, locationName, API_RESPONSE_LIMIT, API_KEY));
    }

    private URI createLocationUri(double longitude, double latitude) {
        return URI.create(String.format(OPEN_WEATHER_URL_FORMAT, longitude, latitude, API_KEY, METRIC_SYSTEM));
    }

    private List<Location> parseLocations(String body) {
        try {
            JsonNode rootNode = objectMapper.readTree(body);

            List<Location> locations = new ArrayList<>();
            for (JsonNode node : rootNode) {

                String name = extractName(node);
                double lon = node.get("lon").asDouble();
                double lat = node.get("lat").asDouble();

                locations.add(buildLocation(name, lon, lat));
            }

            return locations;

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse location data", e);
            throw new WeatherClientException("Failed to parse location data");
        }
    }

    private HttpResponse<String> getStringHttpResponse(HttpRequest geocodingRequest) {
        try {
            var response = httpClient.send(geocodingRequest, HttpResponse.BodyHandlers.ofString());
            return checkResponse(response);

        } catch (IOException e) {
            logger.error("Weather service error occurred: ", e);
            throw new WeatherClientException("Error occurred while calling the weather service");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread was interrupted: ", e);
            throw new WeatherClientException("Thread was interrupted while calling the weather service");
        }
    }

    private HttpResponse<String> checkResponse(HttpResponse<String> response) {
        return switch (response.statusCode()) {

            case SC_OK -> response;
            case SC_NOT_FOUND -> throw new WeatherClientException("Location not found. 404");

            default ->
                    throw new WeatherClientException("Failed to fetch weather data: Status Code " + response.statusCode());
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

    private static String validateApiKey() {
        String apiKey = System.getenv("OPEN_WEATHER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key for OpenWeatherMap is not set");
        }
        return apiKey;
    }
}
