package com.example.notificationservice.service;

import com.example.notificationservice.exception.NotificationException;
import org.thymeleaf.context.Context;
import java.util.List;

/**
 * Service interface for handling email notifications
 */
public interface EmailService {
    /**
     * Sends a simple text email
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email content
     * @throws NotificationException if sending fails
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Sends emails to multiple recipients
     * @param recipients List of recipient email addresses
     * @param subject Email subject
     * @param body Email content
     * @throws NotificationException if sending fails
     */
    void sendBulkEmails(List<String> recipients, String subject, String body);

    /**
     * Sends an HTML email using a template
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Name of the template to use
     * @param context Template context with variables
     * @throws NotificationException if sending fails
     */
    void sendHtmlEmail(String to, String subject, String templateName, Context context);
} 