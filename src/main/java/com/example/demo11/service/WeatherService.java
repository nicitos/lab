package com.example.demo11.service;

import com.example.demo11.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherData getWeather(String city, Double lat, Double lon) {
        String url = buildUrl(city, lat, lon);
        return restTemplate.getForObject(url, WeatherData.class);
    }

    private String buildUrl(String city, Double lat, Double lon) {
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather?appid=%s&units=metric";
        String url;

        if (city != null && !city.isEmpty()) {
            url = String.format(baseUrl + "&q=%s", apiKey, city);
        } else if (lat != null && lon != null) {
            url = String.format(baseUrl + "&lat=%f&lon=%f", apiKey, lat, lon);
        } else {
            throw new IllegalArgumentException("Укажите город или координаты.");
        }

        return url;
    }
}