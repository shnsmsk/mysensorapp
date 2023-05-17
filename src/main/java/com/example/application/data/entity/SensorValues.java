package com.example.application.data.entity;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SensorValues extends AbstractEntity {

    private LocalDateTime date;
    private Integer temperature;
    private Integer humidity;
    private Integer pressure;

    public SensorValues() {
        // Default constructor required by Jackson for deserialization
    }

    public SensorValues(LocalDateTime date, Number temperature, Number humidity, Number pressure) {
        this.date = date;
        this.temperature = temperature.intValue();
        this.humidity = humidity.intValue();
        this.pressure = pressure.intValue();
    }


    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Integer getTemperature() {
        return temperature;
    }
    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }
    public Integer getHumidity() {
        return humidity;
    }
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }
    public Integer getPressure() {
        return pressure;
    }
    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

}
