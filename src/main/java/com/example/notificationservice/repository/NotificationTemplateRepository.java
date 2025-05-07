package com.example.notificationservice.repository;

import com.example.notificationservice.model.NotificationTemplate;
import com.example.notificationservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    List<NotificationTemplate> findByType(NotificationType type);
} 