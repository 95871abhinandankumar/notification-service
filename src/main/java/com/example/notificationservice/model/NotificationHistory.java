package com.example.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "notification_history")
public class NotificationHistory {
    @Id
    private String id;
    private String userId;
    private String templateId;
    private NotificationType type;
    private String recipient;  // email, phone number, or device token
    private String subject;
    private String content;
    private Map<String, Object> variables;
    private NotificationStatus status;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String providerResponse;  // Response from email/SMS/push provider
    private int retryCount;
    private String campaignId;  // For batch notifications

    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        RETRYING
    }
} 