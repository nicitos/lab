package com.example.demo11.dao;

import com.example.demo11.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Repository
public class WeatherDao {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.units}")
    private String units;

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Запрос к OpenWeatherMap API для получения данных о погоде.
     *
     * @param city Название города (опционально)
     * @param lat  Широта (опционально)
     * @param lon  Долгота (опционально)
     * @return WeatherResponse с данными о погоде
     */
    public WeatherResponse fetchWeatherData(String city, Double lat, Double lon) {
        URI uri = buildUri(city, lat, lon);
        OpenWeatherMapResponse response = restTemplate.getForObject(uri, OpenWeatherMapResponse.class);

        if (response == null) {
            throw new RuntimeException("Не удалось получить данные о погоде");
        }

        return mapToWeatherResponse(response);
    }

    /**
     * Построение URI для запроса к API.
     */
    private URI buildUri(String city, Double lat, Double lon) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("appid", apiKey)
                .queryParam("units", units);

        if (city != null && !city.isEmpty()) {
            builder.queryParam("q", city);
        } else if (lat != null && lon != null) {
            builder.queryParam("lat", lat)
                    .queryParam("lon", lon);
        } else {
            throw new IllegalArgumentException("Необходимо указать либо название города, либо координаты (lat и lon)");
        }

        return builder.build().encode().toUri();
    }

    /**
     * Маппинг ответа OpenWeatherMap в модель WeatherResponse.
     */
    private WeatherResponse mapToWeatherResponse(OpenWeatherMapResponse owmResponse) {
        WeatherResponse weather = new WeatherResponse();
        weather.setCity(owmResponse.getName());
        weather.setTemperature(owmResponse.getMain().getTemp());
        weather.setWindSpeed(owmResponse.getWind().getSpeed());
        weather.setWindDirection(degreesToCardinal(owmResponse.getWind().getDeg()));
        weather.setDescription(owmResponse.getWeather().get(0).getDescription());
        return weather;
    }

    /**
     * Конвертация градусов в направленную сторону ветра.
     */
    private String degreesToCardinal(double degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int)((degrees + 22.5) / 45) % 8;
        return directions[index];
    }

    // Внутренние классы для маппинга ответа OpenWeatherMap
    private static class OpenWeatherMapResponse {
        private String name;
        private Main main;
        private Wind wind;
        private java.util.List<Weather> weather;

        // Геттеры и сеттеры
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Main getMain() { return main; }
        public void setMain(Main main) { this.main = main; }
        public Wind getWind() { return wind; }
        public void setWind(Wind wind) { this.wind = wind; }
        public java.util.List<Weather> getWeather() { return weather; }
        public void setWeather(java.util.List<Weather> weather) { this.weather = weather; }
    }

    private static class Main {
        private double temp;

        public double getTemp() { return temp; }
        public void setTemp(double temp) { this.temp = temp; }
    }

    private static class Wind {
        private double speed;
        private double deg;

        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
        public double getDeg() { return deg; }
        public void setDeg(double deg) { this.deg = deg; }
    }

    private static class Weather {
        private String description;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
