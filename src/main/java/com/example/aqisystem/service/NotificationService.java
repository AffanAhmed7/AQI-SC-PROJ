package com.example.aqisystem.service;

import com.example.aqisystem.model.Notification;
import com.example.aqisystem.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepo;

    public void createNotification(Long userId, Long cityId, String message) {
        try {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setCityId(cityId);
            n.setMessage(message);
            n.setRead(false);
            Notification saved = notificationRepo.save(n);
            System.out.println("Notification created successfully - ID: " + saved.getId() + 
                             ", User ID: " + saved.getUserId() + 
                             ", City ID: " + saved.getCityId());
        } catch (Exception e) {
            System.err.println("Error creating notification for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        try {
            // Try JPQL query first
            List<Notification> notifications = notificationRepo.findByUserId(userId);
            if (notifications == null || notifications.isEmpty()) {
                // Fallback to native query if JPQL doesn't work
                System.out.println("No notifications found with JPQL, trying native query for user: " + userId);
                notifications = notificationRepo.findByUserIdNative(userId);
            }
            return notifications != null ? notifications : List.of();
        } catch (Exception e) {
            System.err.println("Error fetching notifications for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            // Try native query as fallback
            try {
                return notificationRepo.findByUserIdNative(userId);
            } catch (Exception ex) {
                System.err.println("Native query also failed: " + ex.getMessage());
                return List.of();
            }
        }
    }
}

