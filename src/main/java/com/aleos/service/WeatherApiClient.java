package com.aleos.service;

import com.aleos.model.WeatherApiResponse;
import com.aleos.model.entity.Location;

import java.util.List;

/**
 * The WeatherApiClient interface provides methods to interact with a weather data provider API.
 * It allows searching for locations by name and retrieving weather information based on geographic coordinates.
 */
public interface WeatherApiClient {

    List<Location> searchLocationByName(String locationName);

    WeatherApiResponse getWeatherByLocation(double longitude, double latitude);
}
