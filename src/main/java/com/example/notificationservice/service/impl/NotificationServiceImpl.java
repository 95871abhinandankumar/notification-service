package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.model.NotificationRequest;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.FCMService;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.SMSService;
import com.example.notificationservice.model.*;
import com.example.notificationservice.repository.NotificationTemplateRepository;
import com.example.notificationservice.exception.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the NotificationService interface
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private final EmailService emailService;
    private final SMSService smsService;
    private final FCMService fcmService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NotificationTemplateRepository templateRepository;

    @Autowired
    public NotificationServiceImpl(
            EmailService emailService,
            SMSService smsService,
            FCMService fcmService,
            KafkaTemplate<String, Object> kafkaTemplate,
            NotificationTemplateRepository templateRepository) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.fcmService = fcmService;
        this.kafkaTemplate = kafkaTemplate;
        this.templateRepository = templateRepository;
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
            NotificationType type = request.getType();
            switch (type) {
                case EMAIL:
                    sendEmailNotification(request);
                    break;
                case SMS:
                    sendSMSNotification(request);
                    break;
                case PUSH:
                    sendPushNotification(request);
                    break;
                default:
                    throw new NotificationException("Unsupported notification type: " + type);
            }
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
            throw new NotificationException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendNotificationAsync(@Valid NotificationRequest request) {
        validateRequest(request);
        try {
            kafkaTemplate.send("notifications", request);
            log.info("Notification queued successfully for recipient: {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to queue notification: {}", e.getMessage(), e);
            throw new NotificationException("Failed to queue notification", e);
        }
    }

    @Override
    public void processCampaign(@Valid NotificationCampaign campaign) {
        // Implementation for campaign processing
    }

    @Override
    public Optional<NotificationTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    @Override
    public List<NotificationTemplate> getTemplatesByType(NotificationType type) {
        return templateRepository.findByType(type);
    }

    @Override
    public NotificationTemplate updateTemplate(NotificationTemplate template) {
        return templateRepository.save(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public List<NotificationTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        return templateRepository.save(template);
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
        NotificationType type = request.getType();
        if (type == NotificationType.PUSH && !StringUtils.hasText(request.getToken())) {
            throw new NotificationException("Device token is required for push notifications");
        }
    }

    private void sendEmailNotification(NotificationRequest request) {
        try {
            emailService.sendEmail(
                request.getRecipient(),
                request.getSubject(),
                request.getContent()
            );
            log.info("Email sent successfully to {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email to {}", request.getRecipient(), e);
            throw new NotificationException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private void sendSMSNotification(NotificationRequest request) {
        try {
            smsService.sendSMS(
                request.getRecipient(),
                request.getContent()
            );
            log.info("SMS sent successfully to {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", request.getRecipient(), e);
            throw new NotificationException("Failed to send SMS: " + e.getMessage(), e);
        }
    }

    private void sendPushNotification(NotificationRequest request) {
        try {
            if (request.getToken() != null) {
                fcmService.sendMessageToToken(request);
            } else if (request.getTopic() != null) {
                throw new NotificationException("Topic-based notifications are not supported yet");
            } else {
                throw new NotificationException("Either token or topic must be provided for push notifications");
            }
            log.info("Push notification sent successfully to {}", request.getToken());
        } catch (Exception e) {
            log.error("Failed to send push notification", e);
            throw new NotificationException("Failed to send push notification: " + e.getMessage(), e);
        }
    }
} 