package com.example.aqisystem.weather.util;

import com.example.aqisystem.model.AQIApiResponse;
import com.example.aqisystem.weather.model.ForecastResponse;
import com.example.aqisystem.weather.model.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Component
public class OpenWeatherMapClient {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.weather.current.url}")
    private String currentWeatherUrl;

    @Value("${openweathermap.weather.forecast.url}")
    private String forecastUrl;

    @Value("${openweathermap.airpollution.url}")
    private String airPollutionUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // ------------------ Current Weather ------------------
    public String getCurrentWeather(double lat, double lon, String units) {
        return restTemplate.getForObject(
                currentWeatherUrl, String.class, lat, lon, units, apiKey);
    }

    public WeatherResponse parseCurrentWeather(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        WeatherResponse response = new WeatherResponse();
        response.setCity(node.path("name").asText(""));
        response.setCountry(node.path("sys").path("country").asText(""));
        response.setTemperature(node.path("main").path("temp").asDouble(0.0));
        response.setFeelsLike(node.path("main").path("feels_like").asDouble(0.0));
        response.setHumidity(node.path("main").path("humidity").asInt(0));
        if (node.path("weather").isArray() && node.path("weather").size() > 0) {
            response.setWeather(node.path("weather").get(0).path("main").asText(""));
            response.setIcon(node.path("weather").get(0).path("icon").asText(""));
        }
        return response;
    }

    // ------------------ Forecast ------------------
    public String getForecast(double lat, double lon, String units) {
        return restTemplate.getForObject(
                forecastUrl, String.class, lat, lon, units, apiKey);
    }

    public ForecastResponse parseForecast(String json) throws Exception {
        JsonNode node = mapper.readTree(json);
        ForecastResponse response = new ForecastResponse();
        response.setCity(node.path("city").path("name").asText(""));
        response.setCountry(node.path("city").path("country").asText(""));
        response.setForecastList(new ArrayList<>());

        if (node.path("list").isArray()) {
            for (JsonNode item : node.get("list")) {
                ForecastResponse.ForecastItem f = new ForecastResponse.ForecastItem();
                f.setDateTime(item.path("dt_txt").asText(""));
                f.setTemperature(item.path("main").path("temp").asDouble(0.0));
                f.setFeelsLike(item.path("main").path("feels_like").asDouble(0.0));
                f.setHumidity(item.path("main").path("humidity").asInt(0));
                if (item.path("weather").isArray() && item.path("weather").size() > 0) {
                    f.setWeather(item.path("weather").get(0).path("main").asText(""));
                    f.setIcon(item.path("weather").get(0).path("icon").asText(""));
                }
                response.getForecastList().add(f);
            }
        }
        return response;
    }

    // ------------------ Air Pollution (AQI) ------------------
    public String getAirPollution(double lat, double lon) {
        return restTemplate.getForObject(
                airPollutionUrl, String.class, lat, lon, apiKey);
    }

    public AQIApiResponse parseAirPollution(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        JsonNode list = root.path("list");
        if (!list.isArray() || list.size() == 0) {
            return null;
        }
        JsonNode item = list.get(0);

        AQIApiResponse response = new AQIApiResponse();
        AQIApiResponse.Data data = new AQIApiResponse.Data();

        int aqiScale = item.path("main").path("aqi").asInt(0); // 1–5 scale
        data.setAqi(aqiScale);
        data.setCategory(mapCategory(aqiScale));

        AQIApiResponse.Pollutants pollutants = new AQIApiResponse.Pollutants();
        JsonNode comp = item.path("components");
        pollutants.setPm25(comp.path("pm2_5").asDouble(0.0));
        pollutants.setPm10(comp.path("pm10").asDouble(0.0));
        pollutants.setCo(comp.path("co").asDouble(0.0));
        pollutants.setNo(comp.path("no").asDouble(0.0));
        pollutants.setNo2(comp.path("no2").asDouble(0.0));
        pollutants.setO3(comp.path("o3").asDouble(0.0));
        pollutants.setSo2(comp.path("so2").asDouble(0.0));
        pollutants.setNh3(comp.path("nh3").asDouble(0.0));

        data.setPollutants(pollutants);
        response.setData(data);
        return response;
    }

    private String mapCategory(int aqiScale) {
        switch (aqiScale) {
            case 1:
                return "Good";
            case 2:
                return "Fair";
            case 3:
                return "Moderate";
            case 4:
                return "Poor";
            case 5:
                return "Very Poor";
            default:
                return "Unknown";
        }
    }

    // ------------------ Geocoding ------------------
    /**
     * Query OpenWeatherMap direct geocoding endpoint and return raw JSON array
     * response as String.
     * We build the URL manually to avoid issues with property templates.
     */
    public String getGeocode(String cityName, int limit) {
        try {
            String q = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
            String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + q
                    + "&limit=" + Math.max(1, limit)
                    + "&appid=" + apiKey;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            // log but return null on error
            System.err.println("Geocoding request failed for '" + cityName + "': " + e.getMessage());
            return null;
        }
    }

    public String getReverseGeocode(double lat, double lon, int limit) {
        try {
            String url = "http://api.openweathermap.org/geo/1.0/reverse?lat=" + lat
                    + "&lon=" + lon
                    + "&limit=" + Math.max(1, limit)
                    + "&appid=" + apiKey;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Reverse geocoding request failed for " + lat + "," + lon + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse a direct geocoding JSON array and return first match as LatLon object
     * (or null).
     * Example response: [ { "name":"Karachi", "lat":24.86, "lon":67.00,
     * "country":"PK" }, ... ]
     */
    public LatLon parseGeocode(String json) throws Exception {
        if (json == null || json.trim().isEmpty())
            return null;
        JsonNode root = mapper.readTree(json);
        if (!root.isArray() || root.size() == 0)
            return null;
        JsonNode first = root.get(0);
        LatLon out = new LatLon();
        out.setLat(first.path("lat").asDouble());
        out.setLon(first.path("lon").asDouble());
        out.setName(first.path("name").asText(""));
        out.setCountry(first.path("country").asText(""));
        return out;
    }

    // small DTO to return parsed geocode result
    public static class LatLon {
        private double lat;
        private double lon;
        private String name;
        private String country;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    // ------------------ API Key Accessor ------------------
    public String getApiKey() {
        return this.apiKey;
    }
}
