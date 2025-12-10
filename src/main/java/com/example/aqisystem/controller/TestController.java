package com.example.aqisystem.controller;

import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.model.City;
import com.example.aqisystem.model.User;
import com.example.aqisystem.repository.AQIRepository;
import com.example.aqisystem.repository.CityRepository;
import com.example.aqisystem.repository.NotificationRepository;
import com.example.aqisystem.service.AQIWeatherService;
import com.example.aqisystem.service.EmailService;
import com.example.aqisystem.service.NotificationService;
import com.example.aqisystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AQIWeatherService aqiWeatherService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private AQIRepository aqiRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${aqi.alert.threshold:101}")
    private int aqiThreshold;

    /**
     * Test endpoint to send a simple test email
     * GET /api/test/email?to=your-email@example.com
     */
    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestParam String to) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String subject = "🧪 Test Email from AQI System";
            String body = "This is a test email from the AQI Monitoring System.\n\n" +
                         "If you received this email, the email notification system is working correctly!";
            
            emailService.sendEmail(to, subject, body);
            
            response.put("success", true);
            response.put("message", "Test email sent successfully to: " + to);
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send test email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint to manually trigger the scheduler logic for a specific user
     * GET /api/test/trigger-alert?userId=1
     */
    @GetMapping("/trigger-alert")
    public ResponseEntity<Map<String, Object>> triggerAlertForUser(@RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = userService.getUser(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "User not found with ID: " + userId);
                return ResponseEntity.status(404).body(response);
            }

            City city = user.getCity();
            if (city == null) {
                response.put("success", false);
                response.put("message", "User " + user.getEmail() + " has no default city set");
                return ResponseEntity.status(400).body(response);
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "User has no email address");
                return ResponseEntity.status(400).body(response);
            }

            // Fetch latest AQI
            AQIRecord latest = aqiWeatherService.getLatestAQIByCity(city);
            if (latest == null) {
                response.put("success", false);
                response.put("message", "Failed to fetch AQI for city: " + city.getName());
                return ResponseEntity.status(500).body(response);
            }

            response.put("userEmail", user.getEmail());
            response.put("city", city.getName());
            response.put("currentAQI", latest.getAqiIndex());
            response.put("aqiCategory", latest.getCategory());
            response.put("threshold", aqiThreshold);
            response.put("exceedsThreshold", latest.getAqiIndex() >= aqiThreshold);

            // If AQI exceeds threshold, send alert
            if (latest.getAqiIndex() >= aqiThreshold) {
                String msg = "⚠ AQI Alert! The air quality for " +
                        city.getName() + " is " + latest.getAqiIndex() +
                        " (" + latest.getCategory() + ")";

                notificationService.createNotification(user.getId(), city.getId(), msg);
                aqiWeatherService.sendAQIEmail(user, latest);

                response.put("alertSent", true);
                response.put("notificationCreated", true);
                response.put("emailSent", true);
                response.put("message", "Alert sent successfully!");
            } else {
                response.put("alertSent", false);
                response.put("message", "AQI is within safe range. No alert sent.");
            }

            response.put("success", true);
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint to trigger alerts for all users (simulates scheduler run)
     * GET /api/test/trigger-all-alerts
     */
    @GetMapping("/trigger-all-alerts")
    public ResponseEntity<Map<String, Object>> triggerAllAlerts() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int totalUsers = 0;
            int processedUsers = 0;
            int alertsSent = 0;
            int errorsEncountered = 0;

            for (User user : userService.getAllUsers()) {
                totalUsers++;
                try {
                    City city = user.getCity();
                    if (city == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                        continue;
                    }

                    AQIRecord latest = aqiWeatherService.getLatestAQIByCity(city);
                    if (latest == null) {
                        errorsEncountered++;
                        continue;
                    }

                    processedUsers++;

                    if (latest.getAqiIndex() >= aqiThreshold) {
                        String msg = "⚠ AQI Alert! The air quality for " +
                                city.getName() + " is " + latest.getAqiIndex() +
                                " (" + latest.getCategory() + ")";

                        notificationService.createNotification(user.getId(), city.getId(), msg);
                        aqiWeatherService.sendAQIEmail(user, latest);
                        alertsSent++;
                    }
                } catch (Exception e) {
                    errorsEncountered++;
                    System.err.println("Error processing user " + user.getEmail() + ": " + e.getMessage());
                }
            }

            response.put("success", true);
            response.put("totalUsers", totalUsers);
            response.put("processedUsers", processedUsers);
            response.put("alertsSent", alertsSent);
            response.put("errorsEncountered", errorsEncountered);
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint to check email configuration
     * GET /api/test/email-config
     */
    @GetMapping("/email-config")
    public ResponseEntity<Map<String, Object>> checkEmailConfig() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Try to get mail sender bean to verify configuration
            response.put("emailServiceAvailable", emailService != null);
            response.put("message", "Email service is configured. Use /api/test/email to test sending.");
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Email service not available: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test endpoint to verify database storage
     * GET /api/test/db-status
     */
    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkDatabaseStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check users
            long userCount = userService.getAllUsers().size();
            
            // Check cities
            long cityCount = cityRepository.count();
            
            // Check AQI readings
            long aqiCount = aqiRepository.count();
            
            // Check notifications
            long notificationCount = notificationRepository.count();
            
            // Get latest AQI record if any
            AQIRecord latestAQI = null;
            if (aqiCount > 0) {
                latestAQI = aqiRepository.findAll().stream()
                    .max((a, b) -> {
                        if (a.getRecordedAt() == null) return -1;
                        if (b.getRecordedAt() == null) return 1;
                        return a.getRecordedAt().compareTo(b.getRecordedAt());
                    })
                    .orElse(null);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("users", userCount);
            data.put("cities", cityCount);
            data.put("aqiReadings", aqiCount);
            data.put("notifications", notificationCount);
            
            if (latestAQI != null) {
                Map<String, Object> latest = new HashMap<>();
                latest.put("id", latestAQI.getId());
                latest.put("city", latestAQI.getCity() != null ? latestAQI.getCity().getName() : "Unknown");
                latest.put("aqiIndex", latestAQI.getAqiIndex());
                latest.put("recordedAt", latestAQI.getRecordedAt());
                data.put("latestAQI", latest);
            }
            
            response.put("success", true);
            response.put("database", "Connected");
            response.put("data", data);
            response.put("message", "Database is accessible and storing data");
            response.put("timestamp", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("database", "Error");
            response.put("message", "Database error: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}

