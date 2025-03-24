package com.example.demo11.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherData {
    private String name;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("weather")
    private Weather[] weather;

    public static class Main {
        private double temp;
        public double getTemp() { return temp; }
    }

    public static class Wind {
        private double speed;
        public double getSpeed() { return speed; }
    }

    public static class Weather {
        private String description;
        public String getDescription() { return description; }
    }

    public String getName() { return name; }
    public Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Weather[] getWeather() { return weather; }
}