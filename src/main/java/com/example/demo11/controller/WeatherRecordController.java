package com.example.demo11.controller;

import com.example.demo11.model.WeatherRecord;
import com.example.demo11.service.WeatherRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Получить все записи о погоде")
    @ApiResponse(responseCode = "200", description = "Список записей успешно получен")
    public List<WeatherRecord> getAllWeatherRecords() {
        return weatherRecordService.getAllWeatherRecords();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить запись о погоде по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись найдена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    public ResponseEntity<WeatherRecord> getWeatherRecordById(
            @PathVariable @Parameter(description = "ID записи") Long id
    ) {
        return weatherRecordService.getWeatherRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новую запись о погоде")
    @ApiResponse(responseCode = "200", description = "Запись успешно создана")
    public WeatherRecord createWeatherRecord(
            @RequestBody @Parameter(description = "Данные записи о погоде") WeatherRecord record
    ) {
        return weatherRecordService.saveWeatherRecord(record);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить запись о погоде по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    public ResponseEntity<WeatherRecord> updateWeatherRecord(
            @PathVariable @Parameter(description = "ID записи") Long id,
            @RequestBody @Parameter(description = "Обновленные данные записи") WeatherRecord recordDetails
    ) {
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
    @Operation(summary = "Удалить запись о погоде по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запись успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    public ResponseEntity<Void> deleteWeatherRecord(
            @PathVariable @Parameter(description = "ID записи") Long id
    ) {
        if (weatherRecordService.getWeatherRecordById(id).isPresent()) {
            weatherRecordService.deleteWeatherRecord(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/city/{cityId}")
    @Operation(summary = "Получить записи о погоде по городу и диапазону дат")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список записей успешно получен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    })
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