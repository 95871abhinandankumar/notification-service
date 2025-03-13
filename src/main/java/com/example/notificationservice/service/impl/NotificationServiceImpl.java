package com.example.notificationservice.service.impl;

import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.FCMService;
import com.example.notificationservice.service.TwilioService;
import com.example.notificationservice.service.UserService;
import com.example.notificationservice.model.*;
import com.example.notificationservice.exception.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.validation.Valid;

/**
 * Implementation of the NotificationService interface
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final EmailService emailService;
    private final FCMService fcmService;
    private final TwilioService twilioService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

    @Autowired
    public NotificationServiceImpl(
            EmailService emailService,
            FCMService fcmService,
            TwilioService twilioService,
            KafkaTemplate<String, Object> kafkaTemplate,
            UserService userService) {
        this.emailService = emailService;
        this.fcmService = fcmService;
        this.twilioService = twilioService;
        this.kafkaTemplate = kafkaTemplate;
        this.userService = userService;
    }

    @Override
    @Retryable(
        value = {NotificationException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendNotification(@Valid NotificationRequest request) {
        validateRequest(request);
        try {
            switch (request.getType()) {
                case EMAIL:
                    sendEmail(request);
                    break;
                case SMS:
                    sendSMS(request);
                    break;
                case PUSH:
                    sendPushNotification(request);
                    break;
                default:
                    throw new NotificationException("Unsupported notification type: " + request.getType());
            }
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage(), e);
            throw new NotificationException("Failed to send notification", e);
        }
    }

    @Override
    public void sendNotificationAsync(@Valid NotificationRequest request) {
        validateRequest(request);
        try {
            kafkaTemplate.send("notifications", request);
            logger.info("Notification queued successfully for recipient: {}", request.getRecipient());
        } catch (Exception e) {
            logger.error("Failed to queue notification: {}", e.getMessage(), e);
            throw new NotificationException("Failed to queue notification", e);
        }
    }

    @Override
    public void processCampaign(@Valid NotificationCampaign campaign) {
        validateCampaign(campaign);
        try {
            campaign.setStatus(NotificationCampaign.CampaignStatus.IN_PROGRESS);
            campaign.setTotalRecipients(campaign.getTargetUserIds().size());

            for (String userId : campaign.getTargetUserIds()) {
                User user = userService.getUserById(userId)
                    .orElseThrow(() -> new NotificationException("User not found: " + userId));

                NotificationRequest request = createNotificationRequest(campaign, user);
                sendNotification(request);
                campaign.setSuccessfulDeliveries(campaign.getSuccessfulDeliveries() + 1);
            }

            campaign.setStatus(NotificationCampaign.CampaignStatus.COMPLETED);
            logger.info("Campaign completed successfully: {}", campaign.getName());
        } catch (Exception e) {
            campaign.setStatus(NotificationCampaign.CampaignStatus.FAILED);
            logger.error("Campaign processing failed: {}", e.getMessage(), e);
            throw new NotificationException("Campaign processing failed", e);
        }
    }

    private void validateRequest(NotificationRequest request) {
        if (request == null) {
            throw new NotificationException("Notification request cannot be null");
        }
        if (request.getType() == null) {
            throw new NotificationException("Notification type cannot be null");
        }
        if (!StringUtils.hasText(request.getRecipient())) {
            throw new NotificationException("Recipient cannot be empty");
        }
        if (request.getType() == NotificationType.PUSH && !StringUtils.hasText(request.getToken())) {
            throw new NotificationException("Device token is required for push notifications");
        }
    }

    private void validateCampaign(NotificationCampaign campaign) {
        if (campaign == null) {
            throw new NotificationException("Campaign cannot be null");
        }
        if (campaign.getType() == null) {
            throw new NotificationException("Campaign type cannot be null");
        }
        if (campaign.getTargetUserIds() == null || campaign.getTargetUserIds().isEmpty()) {
            throw new NotificationException("Campaign must have target users");
        }
    }

    private void sendEmail(NotificationRequest request) {
        emailService.sendEmail(
            request.getRecipient(),
            request.getSubject(),
            request.getContent()
        );
    }

    private void sendSMS(NotificationRequest request) {
        twilioService.sendSMS(
            request.getRecipient(),
            request.getContent()
        );
    }

    private void sendPushNotification(NotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (Exception e) {
            throw new NotificationException("Failed to send push notification", e);
        }
    }

    private NotificationRequest createNotificationRequest(NotificationCampaign campaign, User user) {
        NotificationRequest request = new NotificationRequest();
        request.setType(campaign.getType());
        request.setSubject(campaign.getVariables().get("subject").toString());
        request.setContent(campaign.getVariables().get("content").toString());

        switch (campaign.getType()) {
            case EMAIL:
                request.setRecipient(user.getEmail());
                break;
            case SMS:
                request.setRecipient(user.getPhoneNumber());
                break;
            case PUSH:
                user.getNotificationPreferences().stream()
                    .filter(pref -> pref.getType() == NotificationType.PUSH)
                    .findFirst()
                    .ifPresent(pref -> request.setToken(pref.getDeviceToken()));
                break;
            default:
                throw new NotificationException("Unsupported notification type: " + campaign.getType());
        }

        return request;
    }
} 