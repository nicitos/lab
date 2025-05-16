package com.example.demo11.controller;

import com.example.demo11.model.WeatherRecord;
import com.example.demo11.service.AccessCounter;
import com.example.demo11.service.CityService;
import com.example.demo11.service.WeatherRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/weather-records")
public class WeatherRecordController {
    private final WeatherRecordService weatherRecordService;
    private final AccessCounter accessCounter;
    private final CityService cityService; // Добавляем CityService

    @Autowired
    public WeatherRecordController(WeatherRecordService weatherRecordService, AccessCounter accessCounter, CityService cityService) {
        this.weatherRecordService = weatherRecordService;
        this.accessCounter = accessCounter;
        this.cityService = cityService; // Внедряем CityService
    }

    // REST API эндпоинты
    @GetMapping
    @Operation(summary = "Получить все записи о погоде")
    @ApiResponse(responseCode = "200", description = "Список записей успешно получен")
    public ResponseEntity<List<WeatherRecord>> getAllWeatherRecords() {
        return ResponseEntity.ok(weatherRecordService.getAllWeatherRecords());
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
    public ResponseEntity<WeatherRecord> createWeatherRecord(
            @RequestBody @Parameter(description = "Данные записи о погоде") WeatherRecord record
    ) {
        return ResponseEntity.ok(weatherRecordService.saveWeatherRecord(record));
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
    public ResponseEntity<List<WeatherRecord>> getWeatherRecordsByCityAndDate(
            @PathVariable @Parameter(description = "ID города") Long cityId,
            @RequestParam @Parameter(description = "Начальная дата в формате ISO") String startDate,
            @RequestParam @Parameter(description = "Конечная дата в формате ISO") String endDate
    ) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            return ResponseEntity.ok(weatherRecordService.getWeatherRecordsByCityAndDateRange(cityId, start, end));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Неверный формат даты. Используйте ISO формат, например, 2025-04-21T00:00:00");
        }
    }

    @GetMapping("/access-count")
    @Operation(summary = "Получить количество обращений к сервису погодных записей")
    @ApiResponse(responseCode = "200", description = "Количество обращений успешно получено")
    public ResponseEntity<Long> getAccessCount() {
        return ResponseEntity.ok(accessCounter.getCount());
    }

    @PostMapping("/access-count/reset")
    @Operation(summary = "Сбросить счетчик обращений к сервису погодных записей")
    @ApiResponse(responseCode = "200", description = "Счетчик успешно сброшен")
    public ResponseEntity<Void> resetAccessCount() {
        accessCounter.reset();
        return ResponseEntity.ok().build();
    }

    // UI методы
    @GetMapping("/ui/records")
    public String getRecordsPage(Model model) {
        List<WeatherRecord> records = weatherRecordService.getAllWeatherRecords();
        model.addAttribute("records", records);
        return "weatherRecords";
    }

    @GetMapping("/ui/records/add")
    public String addWeatherRecordPage(Model model) {
        model.addAttribute("record", new WeatherRecord());
        model.addAttribute("cities", cityService.getAllCities()); // Теперь cityService доступен
        return "addWeatherRecord";
    }

    @PostMapping("/ui/records/add")
    public String addWeatherRecordSubmit(@ModelAttribute WeatherRecord record) {
        weatherRecordService.saveWeatherRecord(record);
        return "redirect:/weather-records/ui/records";
    }

    @GetMapping("/ui/records/edit/{id}")
    public String editWeatherRecordPage(@PathVariable Long id, Model model) {
        weatherRecordService.getWeatherRecordById(id).ifPresent(record -> model.addAttribute("record", record));
        model.addAttribute("cities", cityService.getAllCities()); // Теперь cityService доступен
        return "editWeatherRecord";
    }

    @PostMapping("/ui/records/edit/{id}")
    public String editWeatherRecordSubmit(@PathVariable Long id, @ModelAttribute WeatherRecord recordDetails) {
        weatherRecordService.getWeatherRecordById(id).ifPresent(record -> {
            record.setTemperature(recordDetails.getTemperature());
            record.setWindSpeed(recordDetails.getWindSpeed());
            record.setCloudiness(recordDetails.getCloudiness());
            record.setTimestamp(recordDetails.getTimestamp());
            record.setCity(recordDetails.getCity());
            weatherRecordService.saveWeatherRecord(record);
        });
        return "redirect:/weather-records/ui/records";
    }

    @PostMapping("/ui/records/delete/{id}")
    public String deleteWeatherRecordUi(@PathVariable Long id) {
        weatherRecordService.getWeatherRecordById(id).ifPresent(record -> weatherRecordService.deleteWeatherRecord(id));
        return "redirect:/weather-records/ui/records";
    }
}