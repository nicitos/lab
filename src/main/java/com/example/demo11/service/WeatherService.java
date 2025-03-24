package com.example.demo11.service;

import com.example.demo11.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Value("${api.key}")
    private String apiKey;

    public WeatherData getWeather(String city) {
        String url = String.format("%s?q=%s&units=metric&appid=%s",
                BASE_URL,
                city,
                apiKey);

        return restTemplate.getForObject(url, WeatherData.class);
    }

    public WeatherData getWeatherByCoordinates(double lat, double lon) {
        String url = String.format("%s?lat=%f&lon=%f&units=metric&appid=%s",
                BASE_URL,
                lat,
                lon,
                apiKey);

        return restTemplate.getForObject(url, WeatherData.class);
    }
}