package com.example.demo11.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class WeatherData {
    private double temperature;
    private double windSpeed;
    private int cloudiness;

    @JsonProperty("main")
    private void unpackMain(Map<String, Object> main) {
        this.temperature = Double.parseDouble(main.get("temp").toString());
    }

    @JsonProperty("wind")
    private void unpackWind(Map<String, Object> wind) {
        this.windSpeed = Double.parseDouble(wind.get("speed").toString());
    }

    @JsonProperty("clouds")
    private void unpackClouds(Map<String, Object> clouds) {
        this.cloudiness = Integer.parseInt(clouds.get("all").toString());
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getCloudiness() {
        return cloudiness;
    }
}