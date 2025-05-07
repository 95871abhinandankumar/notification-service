package com.example.notificationservice.controller;

import com.example.notificationservice.model.DeviceRegistration;
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
@RequestMapping("/api/v1/users/{userId}/devices")
@Tag(name = "Device Registration", description = "APIs for managing device registrations for push notifications")
public class DeviceRegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceRegistrationController.class);
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new device for push notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<NotificationResponse> registerDevice(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Device registration details", required = true) 
            @Valid @RequestBody DeviceRegistration device) {
        try {
            logger.info("Registering new device for user {}: platform={}", userId, device.getPlatform());
            device.setUser(userService.getUserById(userId)
                .orElseThrow(() -> new NotificationException("User not found")));
            
            NotificationPreference preference = new NotificationPreference();
            preference.setType(NotificationType.PUSH);
            preference.setEnabled(true);
            preference.setDeviceToken(device.getDeviceToken());
            userService.updateNotificationPreference(userId, preference);
            
            return ResponseEntity.ok(new NotificationResponse(true, "Device registered successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to register device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Update device registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User or device not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{deviceId}")
    public ResponseEntity<NotificationResponse> updateDevice(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Device ID", required = true) @PathVariable Long deviceId,
            @Parameter(description = "Updated device details", required = true) 
            @Valid @RequestBody DeviceRegistration device) {
        try {
            logger.info("Updating device {} for user {}: platform={}", deviceId, userId, device.getPlatform());
            device.setId(deviceId);
            device.setUser(userService.getUserById(userId)
                .orElseThrow(() -> new NotificationException("User not found")));
            
            NotificationPreference preference = new NotificationPreference();
            preference.setType(NotificationType.PUSH);
            preference.setEnabled(true);
            preference.setDeviceToken(device.getDeviceToken());
            userService.updateNotificationPreference(userId, preference);
            
            return ResponseEntity.ok(new NotificationResponse(true, "Device updated successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to update device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Deregister a device")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device deregistered successfully"),
        @ApiResponse(responseCode = "404", description = "User or device not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<NotificationResponse> deregisterDevice(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Device ID", required = true) @PathVariable Long deviceId) {
        try {
            logger.info("Deregistering device {} for user {}", deviceId, userId);
            userService.removeNotificationPreference(userId, NotificationType.PUSH);
            return ResponseEntity.ok(new NotificationResponse(true, "Device deregistered successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to deregister device: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }
} 