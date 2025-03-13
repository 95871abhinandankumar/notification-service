package com.example.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "notification_templates")
public class NotificationTemplate {
    @Id
    private String id;
    private String name;
    private String description;
    private NotificationType type;
    private String subject;
    private String content;
    private String language;
    private boolean isActive;
    private String[] variables;  // List of variables that can be replaced in the template
} 