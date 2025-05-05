package com.example.demo11.controller;

import com.example.demo11.model.City;
import com.example.demo11.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    @Operation(summary = "Получить список всех городов")
    @ApiResponse(responseCode = "200", description = "Список городов успешно получен")
    public List<City> getAllCities() {
        return cityService.getAllCities();
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
    public City createCity(
            @RequestBody @Parameter(description = "Данные города") City city
    ) {
        return cityService.saveCity(city);
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
}