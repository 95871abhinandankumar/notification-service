package com.example.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "notification_providers")
public class NotificationProvider {
    @Id
    private String id;
    private String name;
    private String description;
    private NotificationType type;
    private ProviderType providerType;
    private boolean isActive;
    private boolean isDefault;
    private Map<String, String> credentials;
    private Map<String, Object> configuration;
    private int priority;  // Higher priority providers are tried first
    private int dailyQuota;
    private int currentDailyUsage;
    private String status;

    public enum ProviderType {
        GMAIL,          // For email
        TWILIO,         // For SMS
        FIREBASE,       // For push notifications
        CUSTOM         // For custom implementations
    }
} 