package com.example.notificationservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "notification_campaigns")
public class NotificationCampaign {
    @Id
    private String id;
    private String name;
    private String description;
    private String templateId;
    private NotificationType type;
    private List<String> targetUserIds;
    private Map<String, Object> variables;
    private CampaignStatus status;
    private LocalDateTime scheduledFor;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int totalRecipients;
    private int successfulDeliveries;
    private int failedDeliveries;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CampaignStatus {
        DRAFT,
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
} 