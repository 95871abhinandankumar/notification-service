package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.model.NotificationPreference;
import com.example.notificationservice.service.UserService;
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
@RequestMapping("/api/v1/users/{userId}/notification-preferences")
@Tag(name = "User Notification Preferences", description = "APIs for managing user notification preferences")
public class UserNotificationPreferenceController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserNotificationPreferenceController.class);
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Enable or disable a specific notification type for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification preference updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{type}")
    public ResponseEntity<NotificationResponse> updateNotificationPreference(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Notification type", required = true) @PathVariable NotificationType type,
            @Parameter(description = "Whether to enable the notification", required = true) @RequestParam boolean enabled,
            @Parameter(description = "Device token for push notifications") @RequestParam(required = false) String deviceToken) {
        try {
            logger.info("Updating notification preference for user {}: type={}, enabled={}", userId, type, enabled);
            userService.updateNotificationPreference(userId, type, enabled, deviceToken);
            return ResponseEntity.ok(new NotificationResponse(true, "Notification preference updated successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to update notification preference: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Add or update a notification preference for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification preference added/updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<NotificationResponse> addNotificationPreference(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Notification preference details", required = true) 
            @Valid @RequestBody NotificationPreference preference) {
        try {
            logger.info("Adding notification preference for user {}: type={}", userId, preference.getType());
            userService.updateNotificationPreference(userId, preference);
            return ResponseEntity.ok(new NotificationResponse(true, "Notification preference added successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to add notification preference: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Remove a notification preference for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification preference removed successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{type}")
    public ResponseEntity<NotificationResponse> removeNotificationPreference(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Notification type to remove", required = true) @PathVariable NotificationType type) {
        try {
            logger.info("Removing notification preference for user {}: type={}", userId, type);
            userService.removeNotificationPreference(userId, type);
            return ResponseEntity.ok(new NotificationResponse(true, "Notification preference removed successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to remove notification preference: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }
} 