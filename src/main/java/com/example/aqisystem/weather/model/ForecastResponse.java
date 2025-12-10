package com.example.aqisystem.weather.model;

import java.util.List;

public class ForecastResponse {

    private String city;
    private String country;
    private List<ForecastItem> forecastList;

    public static class ForecastItem {
        private String dateTime;
        private double temperature;
        private double feelsLike;
        private int humidity;
        private String weather;
        private String icon;

        // Getters and Setters
        public String getDateTime() { return dateTime; }
        public void setDateTime(String dateTime) { this.dateTime = dateTime; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getFeelsLike() { return feelsLike; }
        public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
        public String getWeather() { return weather; }
        public void setWeather(String weather) { this.weather = weather; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public List<ForecastItem> getForecastList() { return forecastList; }
    public void setForecastList(List<ForecastItem> forecastList) { this.forecastList = forecastList; }
}
