package com.example.demo11.controller;

import com.example.demo11.model.WeatherResponse;
import com.example.demo11.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * Получение текущей погоды по названию города или координатам.
     *
     * @param city Название города (опционально)
     * @param lat  Широта (опционально)
     * @param lon  Долгота (опционально)
     * @return WeatherResponse с данными о погоде
     */
    @GetMapping
    public WeatherResponse getCurrentWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        return weatherService.getCurrentWeather(city, lat, lon);
    }
}
