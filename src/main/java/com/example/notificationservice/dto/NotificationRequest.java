package com.example.notificationservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.example.notificationservice.model.NotificationType;

import java.util.Map;

@Data
public class NotificationRequest {
    @NotNull(message = "Recipient is required")
    private String recipient;
    
    @NotNull(message = "Type is required")
    private NotificationType type;
    
    private String token;
    private String topic;
    private String title;
    private String body;
    private String subject;
    private String content;
    
    private Map<String, Object> additionalData;
} 