package com.example.application.data.service;

import com.example.application.data.entity.SensorValues;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SensorValuesService {

    @Autowired
    private SensorValuesRepository repository;

   //

    public void saveSensorValue(LocalDateTime date, Integer temperature, Integer humidity, Integer pressure) {
        SensorValues sensorValues = new SensorValues();
        sensorValues.setDate(date);
        sensorValues.setTemperature(temperature);
        sensorValues.setHumidity(humidity);
        sensorValues.setPressure(pressure);
        repository.save(sensorValues);
    }

    public Optional<SensorValues> get(Long id) {
        return repository.findById(id);
    }

    public SensorValues update(SensorValues entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SensorValues> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SensorValues> list(Pageable pageable, Specification<SensorValues> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
