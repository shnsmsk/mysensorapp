package com.example.application.data.service;

import com.example.application.data.entity.SensorValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SensorValuesRepository
        extends
            JpaRepository<SensorValues, Long>,
            JpaSpecificationExecutor<SensorValues> {

}
