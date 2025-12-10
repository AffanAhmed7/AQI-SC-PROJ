package com.example.aqisystem.service;

import com.example.aqisystem.model.AQIApiResponse;
import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.model.City;
import com.example.aqisystem.model.User;
import com.example.aqisystem.repository.AQIRepository;
import com.example.aqisystem.repository.CityRepository;
import com.example.aqisystem.weather.model.ForecastResponse;
import com.example.aqisystem.weather.model.WeatherResponse;
import com.example.aqisystem.weather.util.OpenWeatherMapClient;
import com.example.aqisystem.weather.util.OpenWeatherMapClient.LatLon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AQIWeatherService {

    @Autowired
    private AQIRepository aqiRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private OpenWeatherMapClient weatherClient;

    @Autowired
    private EmailService emailService;

    // ------------------ AQI Section ------------------
    public AQIRecord getLatestAQI(Long cityId) {
        Optional<City> cityOpt = cityRepository.findById(cityId);
        return cityOpt.map(city -> aqiRepository.findTopByCityOrderByRecordedAtDesc(city)).orElse(null);
    }

    public List<AQIRecord> getAQIHistory(Long cityId) {
        Optional<City> cityOpt = cityRepository.findById(cityId);
        return cityOpt.map(city -> aqiRepository.findByCityOrderByRecordedAtDesc(city))
                .orElse(java.util.Collections.emptyList());
    }

    public AQIRecord saveAQIRecord(Long cityId, String category, double pm25, double pm10,
            double co, double no, double no2, double o3, double so2,
            double nh3, int aqiIndex) {
        Optional<City> cityOpt = cityRepository.findById(cityId);
        if (!cityOpt.isPresent())
            return null;
        City city = cityOpt.get();

        AQIRecord record = new AQIRecord();
        record.setCity(city);
        record.setCategory(category);
        record.setPm25(pm25);
        record.setPm10(pm10);
        record.setCo(co);
        record.setNo(no);
        record.setNo2(no2);
        record.setO3(o3);
        record.setSo2(so2);
        record.setNh3(nh3);
        record.setAqiIndex(aqiIndex);
        record.setLatitude(city.getLatitude());
        record.setLongitude(city.getLongitude());
        record.setRecordedAt(LocalDateTime.now());

        return aqiRepository.save(record);
    }

    // ------------------ Weather Section ------------------
    public WeatherResponse getCurrentWeather(double lat, double lon, String units) throws Exception {
        String json = weatherClient.getCurrentWeather(lat, lon, units);
        return weatherClient.parseCurrentWeather(json);
    }

    public ForecastResponse getForecast(double lat, double lon, String units) throws Exception {
        String json = weatherClient.getForecast(lat, lon, units);
        return weatherClient.parseForecast(json);
    }

    // ------------------ City Helpers ------------------
    public City getOrCreateCityByName(String cityName) {
        if (cityName == null || cityName.isEmpty())
            return null;

        Optional<City> existing = cityRepository.findByNameIgnoreCase(cityName.trim());
        if (existing.isPresent())
            return existing.get();

        // Fetch coordinates via geocoding API
        try {
            String json = weatherClient.getGeocode(cityName.trim(), 1);
            LatLon parsed = weatherClient.parseGeocode(json);

            if (parsed == null) {
                System.err.println("Geocoding API returned no result for city: " + cityName);
                return null;
            }

            City city = new City();
            city.setName(parsed.getName() != null ? parsed.getName() : cityName.trim());
            city.setCountry(parsed.getCountry());
            city.setLatitude(parsed.getLat());
            city.setLongitude(parsed.getLon());

            return cityRepository.save(city); // save only after successful geocoding
        } catch (Exception e) {
            System.err.println("Failed to geocode '" + cityName + "': " + e.getMessage());
            return null;
        }
    }

    public City ensureCityCoordinates(City city) {
        if (city == null)
            return null;
        if (city.getLatitude() != null && city.getLongitude() != null && city.getCountry() != null)
            return city;

        try {
            String json = weatherClient.getGeocode(city.getName(), 1);
            LatLon parsed = weatherClient.parseGeocode(json);

            if (parsed != null) {
                city.setLatitude(parsed.getLat());
                city.setLongitude(parsed.getLon());
                if (parsed.getCountry() != null && !parsed.getCountry().trim().isEmpty()) {
                    city.setCountry(parsed.getCountry());
                }
                if (parsed.getName() != null && !parsed.getName().trim().isEmpty()) {
                    city.setName(parsed.getName());
                }
                city = cityRepository.save(city);
            } else {
                System.err.println("Geocoding returned no results for city: " + city.getName());
            }
        } catch (Exception e) {
            System.err.println("Failed to geocode '" + city.getName() + "': " + e.getMessage());
        }
        return city;
    }

    public City getOrCreateCityByCoordinates(String coordinates) {
        if (coordinates == null || !coordinates.contains(",")) {
            return null;
        }
        try {
            String[] parts = coordinates.split(",");
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());

            // Check if city exists by name (optimization - usually better to support
            // lat/lon search in DB but this is okay for now)
            // or we try to reverse geocode first

            String json = weatherClient.getReverseGeocode(lat, lon, 1);
            LatLon parsed = weatherClient.parseGeocode(json); // This might return a list, but parseGeocode handles the
                                                              // first one.

            if (parsed == null) {
                System.err.println("Reverse geocoding returned no result for: " + coordinates);
                return null;
            }

            // Check existing by name to avoid duplicates if possible
            Optional<City> existing = cityRepository.findByNameIgnoreCase(parsed.getName());
            if (existing.isPresent()) {
                // Determine if we should update coordinates? For now just return existing
                return existing.get();
            }

            City city = new City();
            city.setName(parsed.getName() != null ? parsed.getName() : "Unknown City");
            city.setCountry(parsed.getCountry());
            city.setLatitude(lat); // use original requested coordinates or parsed? existing code prefers API
                                   // source usually, but let's stick to parsed if close
            city.setLongitude(lon);

            return cityRepository.save(city);

        } catch (Exception e) {
            System.err.println("Failed to resolve city from coordinates '" + coordinates + "': " + e.getMessage());
            return null;
        }
    }

    public WeatherResponse getCurrentWeatherByCity(City city, String units) {
        try {
            return getCurrentWeather(city.getLatitude(), city.getLongitude(), units);
        } catch (Exception e) {
            System.err.println("Error getting weather for city " + city.getName() + ": " + e.getMessage());
            return null;
        }
    }

    // ------------------ Fetch AQI ------------------
    public AQIRecord fetchAndSaveAQIFromAPI(String cityName) {
        City city = getOrCreateCityByName(cityName);
        return getLatestAQIByCity(city);
    }

    public AQIRecord getLatestAQIByCity(City city) {
        // Ensure coordinates are available
        city = ensureCityCoordinates(city);

        if (city == null || city.getLatitude() == null || city.getLongitude() == null) {
            if (city != null) {
                System.err.println("Cannot fetch AQI: missing coordinates for city " + city.getName());
            } else {
                System.err.println("Cannot fetch AQI: City is null");
            }
            return null;
        }

        try {
            String json = weatherClient.getAirPollution(city.getLatitude(), city.getLongitude());
            AQIApiResponse parsed = weatherClient.parseAirPollution(json);

            if (parsed == null || parsed.getData() == null) {
                System.err.println("Air pollution API returned no data for " + city.getName());
                return null;
            }

            AQIApiResponse.Data data = parsed.getData();
            AQIApiResponse.Pollutants p = data.getPollutants();

            return saveAQIRecord(
                    city.getId(),
                    data.getCategory(),
                    p.getPm25(), p.getPm10(), p.getCo(), p.getNo(),
                    p.getNo2(), p.getO3(), p.getSo2(), p.getNh3(),
                    data.getAqi());
        } catch (Exception e) {
            System.err.println("Failed to fetch AQI for city: " + city.getName() + " — " + e.getMessage());
            return null;
        }
    }

    // ------------------ Email ------------------
    public void sendAQIEmail(User user, AQIRecord latest) {
        if (user == null || latest == null)
            return;
        String subject = "🌤 AQI Update for " + latest.getCity().getName();
        String body = generateMessage(latest.getAqiIndex(), latest.getCategory());
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    private String generateMessage(int aqi, String category) {
        switch (aqi) {
            case 1:
                return "Air quality is Good. Enjoy your day!";
            case 2:
                return "Air quality is Fair. Sensitive groups should take care.";
            case 3:
                return "Air quality is Moderate. Consider limiting outdoor activity.";
            case 4:
                return "Air quality is Poor. Better to stay indoors.";
            case 5:
                return "Air quality is Very Poor. Avoid going outside!";
            default:
                return "Air quality data unavailable.";
        }
    }

    // ------------------ Controller Wrappers ------------------
    public AQIRecord getLatestAQIByCityName(String cityName) {
        AQIRecord latest = fetchAndSaveAQIFromAPI(cityName); // always fetch from API
        return latest;
    }

    public WeatherResponse getCurrentWeatherByCityName(String cityName, String units) throws Exception {
        City city = getOrCreateCityByName(cityName);
        city = ensureCityCoordinates(city);
        if (city == null || city.getLatitude() == null || city.getLongitude() == null)
            return null;
        return getCurrentWeather(city.getLatitude(), city.getLongitude(), units);
    }

    public ForecastResponse getForecastByCity(City city, String units) throws Exception {
        if (city == null || city.getLatitude() == null || city.getLongitude() == null)
            return null;
        return getForecast(city.getLatitude(), city.getLongitude(), units);
    }
}
