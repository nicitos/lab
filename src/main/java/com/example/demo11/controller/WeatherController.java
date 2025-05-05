package com.example.demo11.controller;

import com.example.demo11.dto.WeatherData;
import com.example.demo11.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    @Operation(summary = "Получить текущую погоду по городу или координатам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные о погоде успешно получены"),
            @ApiResponse(responseCode = "400", description = "Неверный город, координаты или API-ключ"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public WeatherData getWeather(
            @RequestParam(required = false) @Parameter(description = "Название города") String city,
            @RequestParam(required = false) @Parameter(description = "Широта") Double lat,
            @RequestParam(required = false) @Parameter(description = "Долгота") Double lon
    ) {
        return weatherService.getWeather(city, lat, lon);
    }
}