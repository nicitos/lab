package com.example.demo11.controller;

import com.example.demo11.model.WeatherData;
import com.example.demo11.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public String getWeather(@RequestParam String city) {
        WeatherData weatherData = weatherService.getWeather(city);
        return String.format("Город: %s\nТемпература: %.1f°C\nСкорость ветра: %.1f м/с\nОписание погоды: %s",
                weatherData.getName(),
                weatherData.getMain().getTemp(),
                weatherData.getWind().getSpeed(),
                weatherData.getWeather()[0].getDescription());
    }

    @GetMapping("/coordinates")
    public String getWeatherByCoordinates(@RequestParam double lat, @RequestParam double lon) {
        WeatherData weatherData = weatherService.getWeatherByCoordinates(lat, lon);
        return String.format("Город: %s\nТемпература: %.1f°C\nСкорость ветра: %.1f м/с\nОписание погоды: %s",
                weatherData.getName(),
                weatherData.getMain().getTemp(),
                weatherData.getWind().getSpeed(),
                weatherData.getWeather()[0].getDescription());
    }
}