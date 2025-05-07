package com.example.notificationservice.service;

import com.example.notificationservice.model.NotificationRequest;
import com.example.notificationservice.model.NotificationCampaign;
import com.example.notificationservice.exception.NotificationException;
import com.example.notificationservice.model.NotificationTemplate;
import com.example.notificationservice.model.NotificationType;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling notifications across different channels (Email, SMS, Push)
 */
public interface NotificationService {
    /**
     * Sends a notification synchronously
     * @param request The notification request containing type, recipient, and content
     * @throws NotificationException if sending fails
     */
    void sendNotification(NotificationRequest request);

    /**
     * Queues a notification for asynchronous processing
     * @param request The notification request to be queued
     * @throws NotificationException if queueing fails
     */
    void sendNotificationAsync(NotificationRequest request);

    /**
     * Processes a notification campaign
     * @param campaign The campaign details including target users and content
     * @throws NotificationException if campaign processing fails
     */
    void processCampaign(NotificationCampaign campaign);

    NotificationTemplate createTemplate(@Valid NotificationTemplate template);

    Optional<NotificationTemplate> getTemplateById(Long id);

    List<NotificationTemplate> getTemplatesByType(NotificationType type);

    NotificationTemplate updateTemplate(NotificationTemplate template);

    void deleteTemplate(Long id);

    List<NotificationTemplate> getAllTemplates();
} 