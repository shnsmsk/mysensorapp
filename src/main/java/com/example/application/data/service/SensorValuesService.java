package com.example.application.data.service;

import com.example.application.data.entity.SensorValues;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SensorValuesService {

    private final SensorValuesRepository repository;

    public SensorValuesService(SensorValuesRepository repository) {
        this.repository = repository;
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
