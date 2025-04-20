package com.example.demo11.service;

import com.example.demo11.model.WeatherRecord;
import com.example.demo11.DAO.WeatherRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherRecordService {
    private final WeatherRecordRepository weatherRecordRepository;

    @Autowired
    public WeatherRecordService(WeatherRecordRepository weatherRecordRepository) {
        this.weatherRecordRepository = weatherRecordRepository;
    }

    public WeatherRecord saveWeatherRecord(WeatherRecord record) {
        return weatherRecordRepository.save(record);
    }

    public List<WeatherRecord> getAllWeatherRecords() {
        return weatherRecordRepository.findAll();
    }

    public Optional<WeatherRecord> getWeatherRecordById(Long id) {
        return weatherRecordRepository.findById(id);
    }

    public void deleteWeatherRecord(Long id) {
        weatherRecordRepository.deleteById(id);
    }
}