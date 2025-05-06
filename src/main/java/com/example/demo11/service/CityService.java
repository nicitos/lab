package com.example.demo11.service;

import com.example.demo11.DAO.CityRepository;
import com.example.demo11.cache.CacheManager;
import com.example.demo11.model.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CacheManager cacheManager;

    @Autowired
    public CityService(CityRepository cityRepository, CacheManager cacheManager) {
        this.cityRepository = cityRepository;
        this.cacheManager = cacheManager;
    }

    public City saveCity(City city) {
        City savedCity = cityRepository.save(city);
        if (savedCity.getId() != null) {
            cacheManager.clearCityCache(savedCity.getId());
            cacheManager.clearAllCitiesCache();
        }
        return savedCity;
    }

    public List<City> getAllCities() {
        String cacheKey = "allCities";
        List<Object> cachedCities = cacheManager.getAllCities(cacheKey);
        if (cachedCities != null) {
            return cachedCities.stream().map(obj -> (City) obj).toList();
        }

        List<City> cities = cityRepository.findAll();
        cacheManager.putAllCities(cacheKey, cities.stream().map(obj -> (Object) obj).toList());
        return cities;
    }

    public Optional<City> getCityById(Long id) {
        Object cachedCity = cacheManager.getCity(id);
        if (cachedCity != null) {
            return Optional.of((City) cachedCity);
        }

        Optional<City> city = cityRepository.findById(id);
        city.ifPresent(c -> cacheManager.putCity(id, c));
        return city;
    }

    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
        cacheManager.clearCityCache(id);
        cacheManager.clearAllCitiesCache();
    }

    public List<City> saveAllCities(List<City> cities) {
        if (cities.stream().anyMatch(city -> city.getName() == null || city.getName().isEmpty())) {
            throw new IllegalArgumentException("City name cannot be null or empty");
        }
        List<City> savedCities = cityRepository.saveAll(cities);
        cacheManager.clearAllCitiesCache();
        savedCities.forEach(city -> cacheManager.clearCityCache(city.getId()));
        return savedCities;
    }
}