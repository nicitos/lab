package com.example.demo11.controller;

import com.example.demo11.model.City;
import com.example.demo11.service.AccessCounter;
import com.example.demo11.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;
    private final AccessCounter accessCounter;

    @Autowired
    public CityController(CityService cityService, AccessCounter accessCounter) {
        this.cityService = cityService;
        this.accessCounter = accessCounter;
    }

    // REST API эндпоинты
    @GetMapping
    @Operation(summary = "Получить список всех городов")
    @ApiResponse(responseCode = "200", description = "Список городов успешно получен")
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить город по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город найден"),
            @ApiResponse(responseCode = "404", description = "Город не найден")
    })
    public ResponseEntity<City> getCityById(
            @PathVariable @Parameter(description = "ID города") Long id
    ) {
        return cityService.getCityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый город")
    @ApiResponse(responseCode = "200", description = "Город успешно создан")
    public ResponseEntity<City> createCity(
            @RequestBody @Parameter(description = "Данные города") City city
    ) {
        return ResponseEntity.ok(cityService.saveCity(city));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить город по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Город не найден")
    })
    public ResponseEntity<City> updateCity(
            @PathVariable @Parameter(description = "ID города") Long id,
            @RequestBody @Parameter(description = "Обновленные данные города") City cityDetails
    ) {
        return cityService.getCityById(id)
                .map(city -> {
                    city.setName(cityDetails.getName());
                    return ResponseEntity.ok(cityService.saveCity(city));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить город по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Город успешно удален"),
            @ApiResponse(responseCode = "404", description = "Город не найден")
    })
    public ResponseEntity<Void> deleteCity(
            @PathVariable @Parameter(description = "ID города") Long id
    ) {
        if (cityService.getCityById(id).isPresent()) {
            cityService.deleteCity(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько городов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Города успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные города")
    })
    public ResponseEntity<List<City>> createCities(
            @RequestBody @Parameter(description = "Список городов для создания") List<City> cities
    ) {
        try {
            List<City> savedCities = cityService.saveAllCities(cities);
            return ResponseEntity.ok(savedCities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/access-count")
    @Operation(summary = "Получить количество обращений к сервису")
    @ApiResponse(responseCode = "200", description = "Количество обращений успешно получено")
    public ResponseEntity<Long> getAccessCount() {
        return ResponseEntity.ok(accessCounter.getCount());
    }

    @PostMapping("/access-count/reset")
    @Operation(summary = "Сбросить счетчик обращений к сервису")
    @ApiResponse(responseCode = "200", description = "Счетчик успешно сброшен")
    public ResponseEntity<Void> resetAccessCount() {
        accessCounter.reset();
        return ResponseEntity.ok().build();
    }

    // UI методы
    @GetMapping("/ui/cities")
    public String getCitiesPage(Model model) {
        List<City> cities = cityService.getAllCities();
        model.addAttribute("cities", cities);
        return "cities";
    }

    @GetMapping("/ui/cities/{id}")
    public String getCityDetailsPage(@PathVariable Long id, Model model) {
        cityService.getCityById(id).ifPresent(city -> model.addAttribute("city", city));
        return "cityDetails";
    }

    @GetMapping("/ui/cities/add")
    public String addCityPage(Model model) {
        model.addAttribute("city", new City());
        return "addCity";
    }

    @PostMapping("/ui/cities/add")
    public String addCitySubmit(@ModelAttribute City city) {
        cityService.saveCity(city);
        return "redirect:/cities/ui/cities";
    }

    @GetMapping("/ui/cities/edit/{id}")
    public String editCityPage(@PathVariable Long id, Model model) {
        cityService.getCityById(id).ifPresent(city -> model.addAttribute("city", city));
        return "editCity";
    }

    @PostMapping("/ui/cities/edit/{id}")
    public String editCitySubmit(@PathVariable Long id, @ModelAttribute City cityDetails) {
        cityService.getCityById(id).ifPresent(city -> {
            city.setName(cityDetails.getName());
            cityService.saveCity(city);
        });
        return "redirect:/cities/ui/cities";
    }

    @PostMapping("/ui/cities/delete/{id}")
    public String deleteCityUi(@PathVariable Long id) {
        cityService.getCityById(id).ifPresent(city -> cityService.deleteCity(id));
        return "redirect:/cities/ui/cities";
    }
}