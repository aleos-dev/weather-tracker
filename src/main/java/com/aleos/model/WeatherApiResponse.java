package com.aleos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {

    @JsonProperty("weather")
    private List<WeatherConditions> weatherConditions;

    @JsonProperty("main")
    private TemperatureInfo temperatureInfo;

    @JsonProperty("wind")
    private WindDetails windDetails;

    private int visibility;

    private boolean isDataAvailable;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherConditions {
        private String main;
        private String description;
        @JsonProperty("icon")
        private String iconCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TemperatureInfo {
        @JsonProperty("temp")
        private int currentTemp;
        @JsonProperty("feels_like")
        private int feelsLikeTemp;
        @JsonProperty("temp_min")
        private int minTemp;
        @JsonProperty("temp_max")
        private int maxTemp;
        @JsonProperty("humidity")
        private int humidityPercentage;
        private int pressure;

        public void setCurrentTemp(double currentTemp) {
            this.currentTemp = (int) Math.ceil(currentTemp);
        }

        public void setFeelsLikeTemp(double feelsLikeTemp) {
            this.feelsLikeTemp = (int) Math.round(feelsLikeTemp);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindDetails {

        private static final NavigableMap<Integer, String> WIND_DIRECTIONS = new TreeMap<>();

        static {
            WIND_DIRECTIONS.put(0, "N");
            WIND_DIRECTIONS.put(22, "NNE");
            WIND_DIRECTIONS.put(45, "NE");
            WIND_DIRECTIONS.put(67, "ENE");
            WIND_DIRECTIONS.put(90, "E");
            WIND_DIRECTIONS.put(112, "ESE");
            WIND_DIRECTIONS.put(135, "SE");
            WIND_DIRECTIONS.put(157, "SSE");
            WIND_DIRECTIONS.put(180, "S");
            WIND_DIRECTIONS.put(202, "SSW");
            WIND_DIRECTIONS.put(225, "SW");
            WIND_DIRECTIONS.put(247, "WSW");
            WIND_DIRECTIONS.put(270, "W");
            WIND_DIRECTIONS.put(292, "WNW");
            WIND_DIRECTIONS.put(315, "NW");
            WIND_DIRECTIONS.put(337, "NNW");
            WIND_DIRECTIONS.put(360, "N");
        }

        @JsonProperty("speed")
        private int windSpeed;

        @JsonProperty("deg")
        private String windDirection;

        public void setWindSpeed(double speed) {
            windSpeed = (int) Math.ceil(speed);
        }

        public void setWindDirection(int deg) {
            windDirection = getWindDirectionByDegree(deg);
        }

        private String getWindDirectionByDegree(int deg) {
            if (deg < 0 || deg > 360) {
                return "no data";
            }
            return WIND_DIRECTIONS.floorEntry(deg).getValue();
        }
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility / 1000;
    }
}
