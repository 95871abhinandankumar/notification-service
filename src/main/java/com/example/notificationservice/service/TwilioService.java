package com.example.notificationservice.service;

import com.example.notificationservice.exception.NotificationException;

/**
 * Service interface for handling SMS notifications using Twilio
 */
public interface TwilioService {
    /**
     * Sends an SMS message to a phone number
     * @param to Recipient phone number
     * @param message SMS content
     * @throws NotificationException if sending fails
     */
    void sendSMS(String to, String message);
} 