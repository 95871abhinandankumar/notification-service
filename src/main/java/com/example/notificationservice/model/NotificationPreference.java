package com.example.notificationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class NotificationPreference {
    @Id
    @GeneratedValue
    private String id;
    
    @ManyToOne
    @NotNull
    private User user;
    
    @NotNull
    private NotificationType type;
    
    private boolean enabled = true;
    
    private String deviceToken;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    
    // Version field for optimistic locking
    private Long version = 0L;
} 