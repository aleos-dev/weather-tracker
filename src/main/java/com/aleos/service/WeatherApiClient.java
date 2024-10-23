package com.aleos.service;

import com.aleos.model.WeatherApiResponse;
import com.aleos.model.entity.Location;

import java.util.List;

public interface WeatherApiClient {

    List<Location> searchLocationByName(String locationName);

    WeatherApiResponse getWeatherByLocation(double longitude, double latitude);
}
