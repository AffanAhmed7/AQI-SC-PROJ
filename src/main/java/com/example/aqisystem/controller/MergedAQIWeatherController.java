package com.example.aqisystem.controller;

import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.service.AQIWeatherService;
import com.example.aqisystem.weather.model.ForecastResponse;
import com.example.aqisystem.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MergedAQIWeatherController {

    @Autowired
    private AQIWeatherService aqiWeatherService;


   @GetMapping("/data")
public ResponseEntity<?> getAllDataByCity(
        @RequestParam String city,
        @RequestParam(defaultValue = "metric") String units
) {
    if (city == null || city.isBlank()) {
        return ResponseEntity.badRequest().body("City name or coordinates are required.");
    }

    try {
        com.example.aqisystem.model.City cityObj;
        String trimmedCity = city.trim();
        
        // Check if input is coordinates (format: "lat,lon")
        if (trimmedCity.matches("-?\\d+(\\.\\d+)?\\s*,\\s*-?\\d+(\\.\\d+)?")) {
            // Handle coordinates
            cityObj = aqiWeatherService.getOrCreateCityByCoordinates(trimmedCity);
            if (cityObj == null) {
                return ResponseEntity.status(404).body("Could not resolve city from coordinates '" + trimmedCity + "'. Please check the format (lat,lon).");
            }
        } else {
            // Handle city name
            cityObj = aqiWeatherService.getOrCreateCityByName(trimmedCity);
            if (cityObj == null) {
                return ResponseEntity.status(404).body("City '" + trimmedCity + "' not found. Please check the city name.");
            }
            
            // Ensure coordinates are valid
            cityObj = aqiWeatherService.ensureCityCoordinates(cityObj);
            if (cityObj == null || cityObj.getLatitude() == null || cityObj.getLongitude() == null) {
                return ResponseEntity.status(404).body("Could not resolve coordinates for city '" + trimmedCity + "'. Please check the city name.");
            }
        }

        // Use the same city object for all API calls to ensure consistency
        AQIRecord aqi = aqiWeatherService.getLatestAQIByCity(cityObj);
        WeatherResponse weather = aqiWeatherService.getCurrentWeatherByCity(cityObj, units);
        ForecastResponse forecast = aqiWeatherService.getForecastByCity(cityObj, units);

        if (aqi == null && weather == null && forecast == null) {
            return ResponseEntity.status(404).body("City data not available from API.");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("aqi", aqi);
        result.put("currentWeather", weather);
        result.put("forecast", forecast);

        // Add city name to response for debugging
        result.put("cityName", cityObj.getName());
        result.put("cityId", cityObj.getId());
        result.put("coordinates", Map.of("lat", cityObj.getLatitude(), "lon", cityObj.getLongitude()));

        return ResponseEntity.ok(result);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error fetching data: " + e.getMessage());
    }
}

}
