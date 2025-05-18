package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.exception.NotificationException;
import com.example.notificationservice.model.NotificationCampaign;
import com.example.notificationservice.model.NotificationHistory;
import com.example.notificationservice.model.NotificationTemplate;
import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.repository.NotificationCampaignRepository;
import com.example.notificationservice.repository.NotificationHistoryRepository;
import com.example.notificationservice.repository.NotificationTemplateRepository;
import com.example.notificationservice.service.EmailService;
import com.example.notificationservice.service.FCMService;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.service.SMSService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the NotificationService interface
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private NotificationCampaignRepository campaignRepository;

    @Autowired
    private NotificationHistoryRepository historyRepository;

    @Override
    public void sendNotification(NotificationRequest request) {
        try {
            switch (request.getType()) {
                case EMAIL:
                    emailService.sendEmail(request.getRecipient(), request.getSubject(), request.getContent());
                    break;
                case SMS:
                    smsService.sendSMS(request.getRecipient(), request.getContent());
                    break;
                case PUSH:
                    fcmService.sendPushNotification(request.getRecipient(), request.getTitle(), request.getContent());
                    break;
                default:
                    throw new NotificationException("Unsupported notification type: " + request.getType());
            }
            // Record notification history
            saveNotificationHistory(request, true, null);
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage(), e);
            saveNotificationHistory(request, false, e.getMessage());
            throw new NotificationException("Failed to send notification", e);
        }
    }

    @Override
    public void sendNotificationAsync(NotificationRequest request) {
        // TODO: Implement async notification sending using a message queue
        throw new UnsupportedOperationException("Async notification sending not implemented yet");
    }

    @Override
    @Transactional
    public void processCampaign(NotificationCampaign campaign) {
        try {
            campaign.setStatus(NotificationCampaign.CampaignStatus.IN_PROGRESS);
            campaignRepository.save(campaign);

            // TODO: Implement campaign processing logic
            // This would typically involve:
            // 1. Getting target users
            // 2. Creating notification requests for each user
            // 3. Sending notifications
            // 4. Updating campaign status

            campaign.setStatus(NotificationCampaign.CampaignStatus.COMPLETED);
            campaignRepository.save(campaign);
        } catch (Exception e) {
            logger.error("Failed to process campaign: {}", e.getMessage(), e);
            campaign.setStatus(NotificationCampaign.CampaignStatus.FAILED);
            campaignRepository.save(campaign);
            throw new NotificationException("Failed to process campaign", e);
        }
    }

    @Override
    public Optional<NotificationCampaign> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    @Override
    public List<NotificationCampaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Override
    @Transactional
    public void updateCampaignStatus(Long id, NotificationCampaign.CampaignStatus status) {
        NotificationCampaign campaign = campaignRepository.findById(id)
            .orElseThrow(() -> new NotificationException("Campaign not found with id: " + id));
        campaign.setStatus(status);
        campaignRepository.save(campaign);
    }

    @Override
    public List<NotificationHistory> getHistoryByUser(String userId, LocalDateTime startDate, LocalDateTime endDate, NotificationType type) {
        if (startDate != null && endDate != null) {
            if (type != null) {
                return historyRepository.findByUserIdAndCreatedAtBetweenAndType(userId, startDate, endDate, type);
            }
            return historyRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
        } else if (type != null) {
            return historyRepository.findByUserIdAndType(userId, type);
        }
        return historyRepository.findByUserId(userId);
    }

    @Override
    public List<NotificationHistory> getHistoryByCampaign(String campaignId) {
        return historyRepository.findByCampaignId(campaignId);
    }

    @Override
    public List<NotificationHistory> getHistoryByStatus(NotificationHistory.NotificationStatus status) {
        return historyRepository.findByStatus(status);
    }

    private void saveNotificationHistory(NotificationRequest request, boolean success, String errorMessage) {
        NotificationHistory history = new NotificationHistory();
        history.setUserId(request.getRecipient());
        history.setType(request.getType());
        history.setContent(request.getContent());
        history.setStatus(success ? NotificationHistory.NotificationStatus.SENT : NotificationHistory.NotificationStatus.FAILED);
        history.setErrorMessage(errorMessage);
        history.setCreatedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Override
    public NotificationTemplate createTemplate(@Valid NotificationTemplate template) {
        return templateRepository.save(template);
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
        if (!templateRepository.existsById(template.getId())) {
            throw new NotificationException("Template not found with id: " + template.getId());
        }
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
} 