package com.example.demo11.service;

import com.example.demo11.DAO.WeatherRecordRepository;
import com.example.demo11.cache.CacheManager;
import com.example.demo11.model.WeatherRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherRecordService {
    private final WeatherRecordRepository weatherRecordRepository;
    private final CacheManager cacheManager;
    private final AccessCounter accessCounter;

    @Autowired
    public WeatherRecordService(WeatherRecordRepository weatherRecordRepository, CacheManager cacheManager, AccessCounter accessCounter) {
        this.weatherRecordRepository = weatherRecordRepository;
        this.cacheManager = cacheManager;
        this.accessCounter = accessCounter;
    }

    public WeatherRecord saveWeatherRecord(WeatherRecord record) {
        accessCounter.increment();
        WeatherRecord savedRecord = weatherRecordRepository.save(record);

        if (savedRecord.getId() != null) {
            cacheManager.clearWeatherRecordCache(savedRecord.getId());
            cacheManager.clearWeatherRecordsByParamsCache("cityId:" + savedRecord.getCity().getId());
            cacheManager.clearAllWeatherRecordsCache();
        }
        return savedRecord;
    }

    public List<WeatherRecord> getAllWeatherRecords() {
        accessCounter.increment();
        String cacheKey = "allWeatherRecords";
        List<Object> cachedRecords = cacheManager.getAllWeatherRecords(cacheKey);
        if (cachedRecords != null) {
            return cachedRecords.stream().map(obj -> (WeatherRecord) obj).toList();
        }

        List<WeatherRecord> records = weatherRecordRepository.findAll();
        cacheManager.putAllWeatherRecords(cacheKey, records.stream().map(obj -> (Object) obj).toList());
        return records;
    }

    public Optional<WeatherRecord> getWeatherRecordById(Long id) {
        accessCounter.increment();
        Object cachedRecord = cacheManager.getWeatherRecord(id);
        if (cachedRecord != null) {
            return Optional.of((WeatherRecord) cachedRecord);
        }

        Optional<WeatherRecord> record = weatherRecordRepository.findById(id);
        record.ifPresent(weatherRecord -> cacheManager.putWeatherRecord(id, weatherRecord));
        return record;
    }

    public void deleteWeatherRecord(Long id) {
        accessCounter.increment();
        weatherRecordRepository.deleteById(id);
        cacheManager.clearWeatherRecordCache(id);
        cacheManager.clearWeatherRecordsByParamsCache("cityId:" + id);
        cacheManager.clearAllWeatherRecordsCache();
    }

    public List<WeatherRecord> getWeatherRecordsByCityAndDateRange(Long cityId, LocalDateTime startDate, LocalDateTime endDate) {
        accessCounter.increment();
        String cacheKey = "cityId:" + cityId + ":startDate:" + startDate + ":endDate:" + endDate;
        List<Object> cachedRecords = cacheManager.getWeatherRecordsByParams(cacheKey);
        if (cachedRecords != null) {
            return cachedRecords.stream().map(obj -> (WeatherRecord) obj).toList();
        }

        List<WeatherRecord> records = weatherRecordRepository.findByCityIdAndDateRange(cityId, startDate, endDate);
        cacheManager.putWeatherRecordsByParams(cacheKey, records.stream().map(obj -> (Object) obj).toList());
        return records;
    }
}