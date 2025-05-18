package com.example.notificationservice.repository;

import com.example.notificationservice.model.NotificationHistory;
import com.example.notificationservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    List<NotificationHistory> findByUserId(String userId);
    List<NotificationHistory> findByUserIdAndType(String userId, NotificationType type);
    List<NotificationHistory> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    List<NotificationHistory> findByUserIdAndCreatedAtBetweenAndType(String userId, LocalDateTime startDate, LocalDateTime endDate, NotificationType type);
    List<NotificationHistory> findByCampaignId(String campaignId);
    List<NotificationHistory> findByStatus(NotificationHistory.NotificationStatus status);
} 