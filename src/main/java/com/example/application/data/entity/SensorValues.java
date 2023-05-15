package com.example.application.data.entity;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SensorValues extends AbstractEntity {

    private LocalDateTime date;
    private Integer temparature;
    private Integer humidity;
    private Integer pressure;

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Integer getTemparature() {
        return temparature;
    }
    public void setTemparature(Integer temparature) {
        this.temparature = temparature;
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
