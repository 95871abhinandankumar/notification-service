package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationCampaign;
import com.example.notificationservice.service.NotificationService;
import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.exception.NotificationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Controller", description = "APIs for sending notifications and managing campaigns")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Send a notification synchronously")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Parameter(description = "Notification request details", required = true)
            @Valid @RequestBody NotificationRequest request) {
        try {
            logger.info("Received notification request for recipient: {}", request.getRecipient());
            notificationService.sendNotification(request);
            logger.info("Notification sent successfully to: {}", request.getRecipient());
            return ResponseEntity.ok(new NotificationResponse(true, "Notification sent successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to send notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Queue a notification for asynchronous processing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification queued successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/send/async")
    public ResponseEntity<NotificationResponse> sendNotificationAsync(
            @Parameter(description = "Notification request details", required = true)
            @Valid @RequestBody NotificationRequest request) {
        try {
            logger.info("Received async notification request for recipient: {}", request.getRecipient());
            notificationService.sendNotificationAsync(request);
            logger.info("Notification queued successfully for: {}", request.getRecipient());
            return ResponseEntity.ok(new NotificationResponse(true, "Notification queued for sending"));
        } catch (NotificationException e) {
            logger.error("Failed to queue notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Process a notification campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/campaign")
    public ResponseEntity<NotificationResponse> createCampaign(
            @Parameter(description = "Campaign details", required = true)
            @Valid @RequestBody NotificationCampaign campaign) {
        try {
            logger.info("Received campaign request: {}", campaign.getName());
            notificationService.processCampaign(campaign);
            logger.info("Campaign processed successfully: {}", campaign.getName());
            return ResponseEntity.ok(new NotificationResponse(true, "Campaign processed successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to process campaign: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }
}