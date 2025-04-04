package com.example.demo11.service;

import com.example.demo11.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherData getWeather(String city, Double lat, Double lon) {
        String url = buildUrl(city, lat, lon);
        try {
            return restTemplate.getForObject(url, WeatherData.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Ошибка запроса к OpenWeatherMap: " + e.getMessage());
        }
    }

    private String buildUrl(String city, Double lat, Double lon) {
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather?appid=%s&units=metric";
        StringBuilder urlBuilder = new StringBuilder();

        if (city != null && !city.isEmpty()) {
            urlBuilder.append(String.format(baseUrl + "&q=%s", apiKey, city));
        } else if (lat != null && lon != null) {
            urlBuilder.append(String.format(baseUrl + "&lat=%f&lon=%f", apiKey, lat, lon));
        } else {
            throw new IllegalArgumentException("Укажите город или координаты.");
        }

        return urlBuilder.toString();
    }
}