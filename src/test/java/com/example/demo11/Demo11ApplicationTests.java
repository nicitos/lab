package com.example.demo11;

import com.example.demo11.controller.WeatherController;
import com.example.demo11.model.WeatherResponse;
import com.example.demo11.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Demo11ApplicationTests {

	@Autowired
	private WeatherController weatherController;

	@MockBean
	private WeatherService weatherService;

	@Test
	void contextLoads() {
		assertEquals("Weather App", "Weather App"); // Пример простого теста
	}

	@Test
	void testGetCurrentWeather() {
		WeatherResponse mockResponse = new WeatherResponse();
		mockResponse.setCity("London");
		mockResponse.setTemperature(15.0);
		mockResponse.setWindSpeed(5.5);
		mockResponse.setWindDirection("NW");
		mockResponse.setDescription("Light rain");

		Mockito.when(weatherService.getCurrentWeather("London", null, null)).thenReturn(mockResponse);

		WeatherResponse response = weatherController.getCurrentWeather("London", null, null);

		assertEquals("London", response.getCity());
		assertEquals(15.0, response.getTemperature());
		assertEquals(5.5, response.getWindSpeed());
		assertEquals("NW", response.getWindDirection());
		assertEquals("Light rain", response.getDescription());
	}
}
