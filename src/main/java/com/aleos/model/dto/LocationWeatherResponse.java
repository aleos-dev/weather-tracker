package com.aleos.model.dto;

import com.aleos.model.WeatherApiResponse;
import lombok.Value;

/**
 * Represents the response containing weather information for a specific location.
 */
@Value(staticConstructor = "of")
public class LocationWeatherResponse {

    String locationName;

    double latitude;

    double longitude;

    WeatherApiResponse weatherDetails;
}
