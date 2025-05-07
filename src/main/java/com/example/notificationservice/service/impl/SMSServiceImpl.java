package com.example.notificationservice.service.impl;

import com.example.notificationservice.service.SMSService;
import com.example.notificationservice.exception.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Slf4j
@Service
public class SMSServiceImpl implements SMSService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @Override
    public void sendSMS(String recipient, String content) {
        try {
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                new PhoneNumber(recipient),
                new PhoneNumber(fromPhoneNumber),
                content
            ).create();
            
            log.info("SMS sent successfully to {} with SID: {}", recipient, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", recipient, e.getMessage(), e);
            throw new NotificationException("Failed to send SMS: " + e.getMessage(), e);
        }
    }
} 