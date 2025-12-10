package com.example.aqisystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aqi_readings")
public class AQIRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    private String category;
    private Double pm25;
    private Double pm10;
    private Double co;
    private Double no;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double nh3;

    @Column(name = "aqi_index")
    private Integer aqiIndex;

    private Double latitude;
    private Double longitude;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    public AQIRecord() {}

    public AQIRecord(City city, String category, Double pm25, Double pm10, Double co, Double no,
                     Double no2, Double o3, Double so2, Double nh3, Integer aqiIndex,
                     Double latitude, Double longitude, LocalDateTime recordedAt) {
        this.city = city;
        this.category = category;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.co = co;
        this.no = no;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.nh3 = nh3;
        this.aqiIndex = aqiIndex;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getPm25() { return pm25; }
    public void setPm25(Double pm25) { this.pm25 = pm25; }
    public Double getPm10() { return pm10; }
    public void setPm10(Double pm10) { this.pm10 = pm10; }
    public Double getCo() { return co; }
    public void setCo(Double co) { this.co = co; }
    public Double getNo() { return no; }
    public void setNo(Double no) { this.no = no; }
    public Double getNo2() { return no2; }
    public void setNo2(Double no2) { this.no2 = no2; }
    public Double getO3() { return o3; }
    public void setO3(Double o3) { this.o3 = o3; }
    public Double getSo2() { return so2; }
    public void setSo2(Double so2) { this.so2 = so2; }
    public Double getNh3() { return nh3; }
    public void setNh3(Double nh3) { this.nh3 = nh3; }
    public Integer getAqiIndex() { return aqiIndex; }
    public void setAqiIndex(Integer aqiIndex) { this.aqiIndex = aqiIndex; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
