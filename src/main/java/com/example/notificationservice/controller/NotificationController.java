package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationRequest;
import com.example.notificationservice.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.concurrent.ExecutionException;

@RestController
public class NotificationController {
    @Autowired
    private FCMService fcmService;

    @PostMapping("/notification")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequest request) throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(request);
        return ResponseEntity.ok("Notification has been sent.");
    }
}