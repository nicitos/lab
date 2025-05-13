package com.example.demo11.service;

import com.example.demo11.DAO.CityRepository;
import com.example.demo11.cache.CacheManager;
import com.example.demo11.model.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CityService cityService;

    @Mock
    private City city;

    @BeforeEach
    public void setUp() {
        // Настраиваем поведение мока City
        when(city.getId()).thenReturn(1L);
        when(city.getName()).thenReturn("Test City");
    }

    @Test
    public void testSaveCity() {
        when(cityRepository.save(city)).thenReturn(city);
        City savedCity = cityService.saveCity(city);
        assertNotNull(savedCity);
        assertEquals("Test City", savedCity.getName());
        verify(cityRepository, times(1)).save(city);
        verify(cacheManager, times(1)).clearCityCache(1L);
        verify(cacheManager, times(1)).clearAllCitiesCache();
    }

    @Test
    public void testGetAllCitiesFromRepository() {
        List<City> cities = Collections.singletonList(city);
        when(cacheManager.getAllCities("allCities")).thenReturn(null);
        when(cityRepository.findAll()).thenReturn(cities);
        List<City> result = cityService.getAllCities();
        assertEquals(1, result.size());
        assertEquals("Test City", result.get(0).getName());
        verify(cacheManager, times(1)).getAllCities("allCities");
        verify(cityRepository, times(1)).findAll();
        verify(cacheManager, times(1)).putAllCities(eq("allCities"), anyList());
    }

    @Test
    public void testGetAllCitiesFromCache() {
        List<City> cities = Collections.singletonList(city);
        when(cacheManager.getAllCities("allCities")).thenReturn(cities.stream().map(obj -> (Object) obj).toList());
        List<City> result = cityService.getAllCities();
        assertEquals(1, result.size());
        assertEquals("Test City", result.get(0).getName());
        verify(cacheManager, times(1)).getAllCities("allCities");
        verify(cityRepository, never()).findAll();
    }

    @Test
    public void testGetCityByIdFromCache() {
        when(cacheManager.getCity(1L)).thenReturn(city);
        Optional<City> foundCity = cityService.getCityById(1L);
        assertTrue(foundCity.isPresent());
        assertEquals("Test City", foundCity.get().getName());
        verify(cacheManager, times(1)).getCity(1L);
        verify(cityRepository, never()).findById(1L);
    }

    @Test
    public void testGetCityByIdFromRepository() {
        when(cacheManager.getCity(1L)).thenReturn(null);
        when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
        Optional<City> foundCity = cityService.getCityById(1L);
        assertTrue(foundCity.isPresent());
        assertEquals("Test City", foundCity.get().getName());
        verify(cacheManager, times(1)).getCity(1L);
        verify(cityRepository, times(1)).findById(1L);
        verify(cacheManager, times(1)).putCity(1L, city);
    }

    @Test
    public void testGetCityByIdNotFound() {
        when(cacheManager.getCity(1L)).thenReturn(null);
        when(cityRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<City> foundCity = cityService.getCityById(1L);
        assertFalse(foundCity.isPresent());
        verify(cacheManager, times(1)).getCity(1L);
        verify(cityRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteCity() {
        doNothing().when(cityRepository).deleteById(1L);
        cityService.deleteCity(1L);
        verify(cityRepository, times(1)).deleteById(1L);
        verify(cacheManager, times(1)).clearCityCache(1L);
        verify(cacheManager, times(1)).clearAllCitiesCache();
    }

    @Test
    public void testSaveAllCities() {
        // Создаём моки для списка городов
        City city1 = mock(City.class);
        City city2 = mock(City.class);
        List<City> cities = Arrays.asList(city1, city2);

        // Настраиваем поведение моков
        when(city1.getName()).thenReturn("City1");
        when(city2.getName()).thenReturn("City2");

        // Настраиваем возвращаемые значения после сохранения
        City savedCity1 = mock(City.class);
        City savedCity2 = mock(City.class);
        when(savedCity1.getId()).thenReturn(1L);
        when(savedCity1.getName()).thenReturn("City1");
        when(savedCity2.getId()).thenReturn(2L);
        when(savedCity2.getName()).thenReturn("City2");
        List<City> savedCities = Arrays.asList(savedCity1, savedCity2);

        when(cityRepository.saveAll(cities)).thenReturn(savedCities);
        List<City> result = cityService.saveAllCities(cities);
        assertEquals(2, result.size());
        assertEquals("City1", result.get(0).getName());
        assertEquals("City2", result.get(1).getName());
        verify(cityRepository, times(1)).saveAll(cities);
        verify(cacheManager, times(1)).putCitiesBulk(savedCities);
    }

    @Test
    public void testSaveAllCitiesWithInvalidCity() {
        // Создаём моки для списка городов
        City city1 = mock(City.class);
        City city2 = mock(City.class);
        List<City> cities = Arrays.asList(city1, city2);

        // Настраиваем поведение моков
        when(city1.getName()).thenReturn("City1");
        when(city2.getName()).thenReturn("");

        assertThrows(IllegalArgumentException.class, () -> cityService.saveAllCities(cities));
        verify(cityRepository, never()).saveAll(anyList());
    }
}