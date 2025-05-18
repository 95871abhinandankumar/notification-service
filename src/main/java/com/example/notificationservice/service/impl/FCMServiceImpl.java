package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.exception.NotificationException;
import com.example.notificationservice.service.FCMService;
import com.example.notificationservice.model.NotificationType;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of the FCMService interface for handling Firebase Cloud Messaging
 */
@Service
public class FCMServiceImpl implements FCMService {
    private static final Logger logger = LoggerFactory.getLogger(FCMServiceImpl.class);

    @Override
    public void sendPushNotification(String recipient, String title, String content) {
        NotificationRequest request = new NotificationRequest();
        request.setRecipient(recipient);
        request.setTitle(title);
        request.setBody(content);
        request.setType(NotificationType.PUSH);
        sendMessageToToken(request);
    }

    @Override
    public void sendMessageToToken(NotificationRequest request) {
        try {
            Message message = getPreconfiguredMessageToToken(request);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(message);
            String response = sendAndGetResponse(message);
            logger.info("Sent message to token. Device token: {}, {} msg {}", 
                request.getToken(), response, jsonOutput);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send push notification: {}", e.getMessage(), e);
            throw new NotificationException("Failed to send push notification", e);
        }
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis())
                .setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setTag(topic)
                        .build())
                .build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setCategory(topic)
                        .setThreadId(topic)
                        .build())
                .build();
    }

    private Message getPreconfiguredMessageToToken(NotificationRequest request) {
        return getPreconfiguredMessageBuilder(request)
                .setToken(request.getToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(NotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();
        return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(notification);
    }
} 