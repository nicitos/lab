package com.example.demo11.controller;

import com.example.demo11.model.WeatherRecord;
import com.example.demo11.service.WeatherRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<WeatherRecord> getWeatherRecordsByCityAndDate(
            @PathVariable Long cityId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return weatherRecordService.getWeatherRecordsByCityAndDateRange(cityId, start, end);
    }
}