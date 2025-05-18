package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.exception.NotificationException;

/**
 * Service interface for handling Firebase Cloud Messaging (FCM) push notifications
 */
public interface FCMService {
    /**
     * Sends a push notification to a specific device token
     * @param request The notification request containing token, title, and body
     * @throws NotificationException if sending fails
     */
    void sendMessageToToken(NotificationRequest request);

    void sendPushNotification(String recipient, String title, String content);
}