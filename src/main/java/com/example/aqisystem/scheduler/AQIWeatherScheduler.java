package com.example.aqisystem.scheduler;

import com.example.aqisystem.model.AQIRecord;
import com.example.aqisystem.model.City;
import com.example.aqisystem.model.User;
import com.example.aqisystem.service.AQIWeatherService;
import com.example.aqisystem.service.NotificationService;
import com.example.aqisystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AQIWeatherScheduler {

    @Autowired
    private AQIWeatherService aqiWeatherService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    // Configurable AQI threshold (default: 101 - Moderate or worse)
    @Value("${aqi.alert.threshold:101}")
    private int aqiThreshold;

    // ------------------ Scheduler ------------------
    // Runs every hour (3600000 ms = 1 hour)
    @Scheduled(fixedRate = 3600000)
    public void checkAQIAndSendAlerts() {
        System.out.println("=== AQI Health Alert Scheduler Started at " + LocalDateTime.now() + " ===");

        List<User> users = userService.getAllUsers();
        System.out.println("Processing " + users.size() + " user(s) for AQI alerts...");

        int alertsSent = 0;
        int errorsEncountered = 0;

        for (User user : users) {
            try {
                // Get user's default city
                City city = user.getCity();
                if (city == null) {
                    System.out.println("Skipping user " + user.getEmail() + " - no default city set");
                    continue;
                }

                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    System.out.println("Skipping user ID " + user.getId() + " - no email address");
                    continue;
                }

                System.out.println("Checking AQI for user: " + user.getEmail() +
                        " in city: " + city.getName());

                // Fetch latest AQI from API
                AQIRecord latest = aqiWeatherService.getLatestAQIByCity(city);
                if (latest == null) {
                    System.err.println("Failed to fetch AQI for city: " + city.getName());
                    errorsEncountered++;
                    continue;
                }

                System.out.println("Current AQI for " + city.getName() + ": " +
                        latest.getAqiIndex() + " (" + latest.getCategory() + ")");

                // Check if AQI exceeds threshold
                if (latest.getAqiIndex() >= aqiThreshold) {
                    String msg = "⚠ AQI Alert! The air quality for " +
                            city.getName() + " is " + latest.getAqiIndex() +
                            " (" + latest.getCategory() + ")";

                    try {
                        // Save notification in database
                        notificationService.createNotification(user.getId(), city.getId(), msg);
                        System.out.println("Notification created for user: " + user.getEmail());

                        // Send email alert
                        aqiWeatherService.sendAQIEmail(user, latest);
                        System.out.println("Email alert sent to: " + user.getEmail() +
                                " - AQI: " + latest.getAqiIndex() +
                                " (Threshold: " + aqiThreshold + ")");
                        alertsSent++;
                    } catch (Exception e) {
                        System.err.println("Error sending alert to user " + user.getEmail() +
                                ": " + e.getMessage());
                        errorsEncountered++;
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("AQI within safe range for " + city.getName() +
                            " (Current: " + latest.getAqiIndex() +
                            ", Threshold: " + aqiThreshold + ")");
                }
            } catch (Exception e) {
                System.err.println("Error processing user " + user.getEmail() +
                        ": " + e.getMessage());
                errorsEncountered++;
                e.printStackTrace();
            }
        }

        System.out.println("=== Scheduler Completed ===");
        System.out.println("Alerts sent: " + alertsSent);
        System.out.println("Errors encountered: " + errorsEncountered);
        System.out.println("Next run scheduled in 1 hour\n");
    }
}
