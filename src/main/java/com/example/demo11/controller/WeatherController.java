package com.example.demo11.controller;

import com.example.demo11.dto.WeatherData;
import com.example.demo11.service.AccessCounter;
import com.example.demo11.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WeatherController {
    private final WeatherService weatherService;
    private final AccessCounter accessCounter;

    @Autowired
    public WeatherController(WeatherService weatherService, AccessCounter accessCounter) {
        this.weatherService = weatherService;
        this.accessCounter = accessCounter;
    }

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

    @GetMapping("/weather/access-count")
    @Operation(summary = "Получить количество обращений к сервису погоды")
    @ApiResponse(responseCode = "200", description = "Количество обращений успешно получено")
    public ResponseEntity<Long> getAccessCount() {
        return ResponseEntity.ok(accessCounter.getCount());
    }

    @PostMapping("/weather/access-count/reset")
    @Operation(summary = "Сбросить счетчик обращений к сервису погоды")
    @ApiResponse(responseCode = "200", description = "Счетчик успешно сброшен")
    public ResponseEntity<Void> resetAccessCount() {
        accessCounter.reset();
        return ResponseEntity.ok().build();
    }
}