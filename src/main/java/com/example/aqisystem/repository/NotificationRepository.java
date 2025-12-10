package com.example.aqisystem.repository;

import com.example.aqisystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Explicit JPQL query to find notifications by user_id column
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    // Native SQL query as backup (uses actual database column name: user_id)
    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<Notification> findByUserIdNative(@Param("userId") Long userId);
}
