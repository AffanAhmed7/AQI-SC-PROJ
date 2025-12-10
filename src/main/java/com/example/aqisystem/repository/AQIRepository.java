package com.example.aqisystem.repository;

import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AQIRepository extends JpaRepository<AQIRecord, Long> {

    // Latest AQI record for a city
    AQIRecord findTopByCityOrderByRecordedAtDesc(City city);

    // All AQI records for a city ordered by time
    List<AQIRecord> findByCityOrderByRecordedAtDesc(City city);

    // All AQI records, latest first
    List<AQIRecord> findAllByOrderByRecordedAtDesc();
}
