package com.example.notificationservice.service.impl;

import com.example.notificationservice.service.TwilioService;
import com.example.notificationservice.exception.NotificationException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of the TwilioService interface
 */
@Service
public class TwilioServiceImpl implements TwilioService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioServiceImpl.class);

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @Override
    public void sendSMS(String to, String message) {
        try {
            Twilio.init(accountSid, authToken);
            Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                message
            ).create();
            logger.info("SMS sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send SMS: {}", e.getMessage(), e);
            throw new NotificationException("Failed to send SMS", e);
        }
    }
} 