package com.example.demo11.controller;

import com.example.demo11.model.WeatherRecord;
import com.example.demo11.service.WeatherRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/weather-records")
public class WeatherRecordController {
    private final WeatherRecordService weatherRecordService;

    @Autowired
    public WeatherRecordController(WeatherRecordService weatherRecordService) {
        this.weatherRecordService = weatherRecordService;
    }

    @GetMapping
    public List<WeatherRecord> getAllWeatherRecords() {
        return weatherRecordService.getAllWeatherRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherRecord> getWeatherRecordById(@PathVariable Long id) {
        return weatherRecordService.getWeatherRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public WeatherRecord createWeatherRecord(@RequestBody WeatherRecord record) {
        return weatherRecordService.saveWeatherRecord(record);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeatherRecord> updateWeatherRecord(@PathVariable Long id, @RequestBody WeatherRecord recordDetails) {
        return weatherRecordService.getWeatherRecordById(id)
                .map(record -> {
                    record.setTemperature(recordDetails.getTemperature());
                    record.setWindSpeed(recordDetails.getWindSpeed());
                    record.setCloudiness(recordDetails.getCloudiness());
                    record.setTimestamp(recordDetails.getTimestamp());
                    record.setCity(recordDetails.getCity());
                    return ResponseEntity.ok(weatherRecordService.saveWeatherRecord(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeatherRecord(@PathVariable Long id) {
        if (weatherRecordService.getWeatherRecordById(id).isPresent()) {
            weatherRecordService.deleteWeatherRecord(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "Получить записи о погоде по городу и диапазону дат")
    public List<WeatherRecord> getWeatherRecordsByCityAndDate(
            @PathVariable @Parameter(description = "ID города") Long cityId,
            @RequestParam @Parameter(description = "Начальная дата в формате ISO") String startDate,
            @RequestParam @Parameter(description = "Конечная дата в формате ISO") String endDate
    ) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            return weatherRecordService.getWeatherRecordsByCityAndDateRange(cityId, start, end);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте ISO формат, например, 2025-04-21T00:00:00");
        }
    }
}