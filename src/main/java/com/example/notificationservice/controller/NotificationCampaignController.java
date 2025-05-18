package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationCampaign;
import com.example.notificationservice.service.NotificationService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@Tag(name = "Notification Campaigns", description = "APIs for managing notification campaigns")
public class NotificationCampaignController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationCampaignController.class);
    
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Create a new notification campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<NotificationResponse> createCampaign(
            @Parameter(description = "Campaign details", required = true) 
            @Valid @RequestBody NotificationCampaign campaign) {
        try {
            logger.info("Creating new campaign: {}", campaign.getName());
            notificationService.processCampaign(campaign);
            return ResponseEntity.ok(new NotificationResponse(true, "Campaign created successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to create campaign: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Get campaign by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign found"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationCampaign> getCampaign(
            @Parameter(description = "Campaign ID", required = true) 
            @PathVariable Long id) {
        try {
            // TODO: Add getCampaignById method to NotificationService
            return ResponseEntity.ok().build();
        } catch (NotificationException e) {
            logger.error("Failed to get campaign: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get all campaigns")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaigns retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<NotificationCampaign>> getAllCampaigns() {
        try {
            // TODO: Add getAllCampaigns method to NotificationService
            return ResponseEntity.ok().build();
        } catch (NotificationException e) {
            logger.error("Failed to get campaigns: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update campaign status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<NotificationResponse> updateCampaignStatus(
            @Parameter(description = "Campaign ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status", required = true) 
            @RequestParam NotificationCampaign.CampaignStatus status) {
        try {
            // TODO: Add updateCampaignStatus method to NotificationService
            return ResponseEntity.ok(new NotificationResponse(true, "Campaign status updated successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to update campaign status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }
} 