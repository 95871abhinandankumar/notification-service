package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.model.NotificationCampaign;
import com.example.notificationservice.model.NotificationHistory;
import com.example.notificationservice.exception.NotificationException;
import com.example.notificationservice.model.NotificationTemplate;
import com.example.notificationservice.model.NotificationType;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
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

    /**
     * Gets a campaign by its ID
     * @param id The campaign ID
     * @return Optional containing the campaign if found
     */
    Optional<NotificationCampaign> getCampaignById(Long id);

    /**
     * Gets all campaigns
     * @return List of all campaigns
     */
    List<NotificationCampaign> getAllCampaigns();

    /**
     * Updates a campaign's status
     * @param id The campaign ID
     * @param status The new status
     * @throws NotificationException if update fails
     */
    void updateCampaignStatus(Long id, NotificationCampaign.CampaignStatus status);

    /**
     * Gets notification history for a user
     * @param userId The user ID
     * @param startDate Optional start date filter
     * @param endDate Optional end date filter
     * @param type Optional notification type filter
     * @return List of notification history entries
     */
    List<NotificationHistory> getHistoryByUser(String userId, LocalDateTime startDate, LocalDateTime endDate, NotificationType type);

    /**
     * Gets notification history for a campaign
     * @param campaignId The campaign ID
     * @return List of notification history entries
     */
    List<NotificationHistory> getHistoryByCampaign(String campaignId);

    /**
     * Gets notification history by status
     * @param status The notification status
     * @return List of notification history entries
     */
    List<NotificationHistory> getHistoryByStatus(NotificationHistory.NotificationStatus status);

    NotificationTemplate createTemplate(@Valid NotificationTemplate template);

    Optional<NotificationTemplate> getTemplateById(Long id);

    List<NotificationTemplate> getTemplatesByType(NotificationType type);

    NotificationTemplate updateTemplate(NotificationTemplate template);

    void deleteTemplate(Long id);

    List<NotificationTemplate> getAllTemplates();
} 