package com.example.demo11.service;

import com.example.demo11.cache.CacheManager;
import com.example.demo11.dto.WeatherData;
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
    private final CacheManager cacheManager;
    private final AccessCounter accessCounter;

    @Autowired
    public WeatherService(RestTemplate restTemplate, CacheManager cacheManager, AccessCounter accessCounter) {
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
        this.accessCounter = accessCounter;
    }

    public WeatherData getWeather(String city, Double lat, Double lon) {
        accessCounter.increment();
        String cacheKey = buildCacheKey(city, lat, lon);

        Object cachedWeatherData = cacheManager.getWeatherData(cacheKey);
        if (cachedWeatherData != null) {
            return (WeatherData) cachedWeatherData;
        }

        String url = buildUrl(city, lat, lon);
        try {
            WeatherData weatherData = restTemplate.getForObject(url, WeatherData.class);
            if (weatherData != null) {
                cacheManager.putWeatherData(cacheKey, weatherData);
            }
            return weatherData;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new IllegalArgumentException("Город или координаты не найдены");
            } else if (e.getStatusCode().value() == 401) {
                throw new IllegalArgumentException("Неверный API-ключ");
            } else {
                throw new RuntimeException("Ошибка при запросе к OpenWeatherMap: " + e.getMessage());
            }
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

    private String buildCacheKey(String city, Double lat, Double lon) {
        if (city != null && !city.isEmpty()) {
            return "city:" + city;
        } else if (lat != null && lon != null) {
            return "lat:" + lat + ":lon:" + lon;
        }
        return "";
    }
}