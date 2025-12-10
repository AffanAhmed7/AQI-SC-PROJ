package com.example.aqisystem.dto;

import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.weather.model.WeatherResponse;
import com.example.aqisystem.weather.model.ForecastResponse;

public class CityDashboardDTO {
    private String city;
    private String country;
    private AQIRecord latestAQI;
    private WeatherResponse currentWeather;
    private ForecastResponse forecast;

    // Getters & Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public AQIRecord getLatestAQI() { return latestAQI; }
    public void setLatestAQI(AQIRecord latestAQI) { this.latestAQI = latestAQI; }
    public WeatherResponse getCurrentWeather() { return currentWeather; }
    public void setCurrentWeather(WeatherResponse currentWeather) { this.currentWeather = currentWeather; }
    public ForecastResponse getForecast() { return forecast; }
    public void setForecast(ForecastResponse forecast) { this.forecast = forecast; }
}
