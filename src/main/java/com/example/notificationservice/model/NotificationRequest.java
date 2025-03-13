package com.example.notificationservice.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@Data
public class NotificationRequest {
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    private String recipient;
    private String subject;
    private String content;
    private Map<String, Object> additionalData;
    
    @NotBlank(message = "Token is required for push notifications")
    private String token;
    private String topic;
    
    @NotBlank(message = "Title is required for push notifications")
    private String title;
    
    @NotBlank(message = "Body is required for push notifications")
    private String body;
} 