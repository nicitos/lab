package com.example.demo11.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CacheManager {

    // Кэш для WeatherRecord по ID
    private final Map<Long, Object> weatherRecordCache = new HashMap<>();

    // Кэш для списка WeatherRecord по запросам с параметрами (cityId, startDate, endDate)
    private final Map<String, List<Object>> weatherRecordsByParamsCache = new HashMap<>();

    // Кэш для City по ID
    private final Map<Long, Object> cityCache = new HashMap<>();

    // Кэш для списка всех городов
    private final Map<String, List<Object>> allCitiesCache = new HashMap<>();

    // Кэш для данных о погоде из OpenWeatherMap
    private final Map<String, Object> weatherDataCache = new HashMap<>();

    // Кэш для списка всех WeatherRecord
    private final Map<String, List<Object>> allWeatherRecordsCache = new HashMap<>();

    // Методы для WeatherRecord
    public void putWeatherRecord(Long id, Object weatherRecord) {
        weatherRecordCache.put(id, weatherRecord);
    }

    public Object getWeatherRecord(Long id) {
        return weatherRecordCache.get(id);
    }

    public void putWeatherRecordsByParams(String key, List<Object> weatherRecords) {
        weatherRecordsByParamsCache.put(key, weatherRecords);
    }

    public List<Object> getWeatherRecordsByParams(String key) {
        return weatherRecordsByParamsCache.get(key);
    }

    public void clearWeatherRecordCache(Long id) {
        weatherRecordCache.remove(id);
    }

    public void clearWeatherRecordsByParamsCache(String key) {
        weatherRecordsByParamsCache.remove(key);
    }

    // Методы для City
    public void putCity(Long id, Object city) {
        cityCache.put(id, city);
    }

    public Object getCity(Long id) {
        return cityCache.get(id);
    }

    public void putAllCities(String key, List<Object> cities) {
        allCitiesCache.put(key, cities);
    }

    public List<Object> getAllCities(String key) {
        return allCitiesCache.get(key);
    }

    public void clearCityCache(Long id) {
        cityCache.remove(id);
    }

    public void clearAllCitiesCache() {
        allCitiesCache.clear();
    }

    // Новый метод для массового добавления городов в кэш
    public void putCitiesBulk(List<?> cities) {
        cities.forEach(city -> {
            try {
                Long id = (Long) city.getClass().getMethod("getId").invoke(city);
                cityCache.put(id, city);
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache city with ID: " + e.getMessage());
            }
        });
        allCitiesCache.put("allCities", cities.stream().map(obj -> (Object) obj).collect(Collectors.toList()));
    }

    // Методы для WeatherData
    public void putWeatherData(String key, Object weatherData) {
        weatherDataCache.put(key, weatherData);
    }

    public Object getWeatherData(String key) {
        return weatherDataCache.get(key);
    }

    public void clearWeatherDataCache(String key) {
        weatherDataCache.remove(key);
    }

    // Методы для списка всех WeatherRecord
    public void putAllWeatherRecords(String key, List<Object> records) {
        allWeatherRecordsCache.put(key, records);
    }

    public List<Object> getAllWeatherRecords(String key) {
        return allWeatherRecordsCache.get(key);
    }

    public void clearAllWeatherRecordsCache() {
        allWeatherRecordsCache.clear();
    }
}