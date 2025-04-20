package com.example.demo11.DAO;


import com.example.demo11.model.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
}