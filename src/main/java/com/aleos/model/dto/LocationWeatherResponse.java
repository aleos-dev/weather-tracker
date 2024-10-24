package com.aleos.model.dto;

import com.aleos.model.WeatherApiResponse;
import lombok.Value;

@Value(staticConstructor = "of")
public class LocationWeatherResponse {

    private String name;

    private double latitude;

    private double longitude;

    private WeatherApiResponse weatherData;
}
