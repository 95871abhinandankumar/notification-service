package com.example.notificationservice.controller;

import com.example.notificationservice.model.NotificationType;
import com.example.notificationservice.model.NotificationTemplate;
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
@RequestMapping("/api/v1/templates")
@Tag(name = "Notification Templates", description = "APIs for managing notification templates")
public class NotificationTemplateController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateController.class);
    
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Create a new notification template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Template created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<NotificationTemplate> createTemplate(
            @Parameter(description = "Template details", required = true) 
            @Valid @RequestBody NotificationTemplate template) {
        try {
            logger.info("Creating new template for type: {}", template.getType());
            NotificationTemplate createdTemplate = notificationService.createTemplate(template);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
        } catch (NotificationException e) {
            logger.error("Failed to create template: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get template by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template found"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationTemplate> getTemplateById(
            @Parameter(description = "Template ID", required = true) 
            @PathVariable Long id) {
        try {
            logger.info("Fetching template with ID: {}", id);
            return notificationService.getTemplateById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NotificationException e) {
            logger.error("Failed to fetch template: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get templates by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationTemplate>> getTemplatesByType(
            @Parameter(description = "Notification type", required = true) 
            @PathVariable NotificationType type) {
        try {
            logger.info("Fetching templates for type: {}", type);
            List<NotificationTemplate> templates = notificationService.getTemplatesByType(type);
            return ResponseEntity.ok(templates);
        } catch (NotificationException e) {
            logger.error("Failed to fetch templates: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificationTemplate> updateTemplate(
            @Parameter(description = "Template ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated template details", required = true) 
            @Valid @RequestBody NotificationTemplate template) {
        try {
            logger.info("Updating template with ID: {}", id);
            template.setId(id);
            NotificationTemplate updatedTemplate = notificationService.updateTemplate(template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (NotificationException e) {
            logger.error("Failed to update template: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Template deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationResponse> deleteTemplate(
            @Parameter(description = "Template ID", required = true) 
            @PathVariable Long id) {
        try {
            logger.info("Deleting template with ID: {}", id);
            notificationService.deleteTemplate(id);
            return ResponseEntity.ok(new NotificationResponse(true, "Template deleted successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to delete template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Get all templates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Templates retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
        try {
            logger.info("Fetching all templates");
            List<NotificationTemplate> templates = notificationService.getAllTemplates();
            return ResponseEntity.ok(templates);
        } catch (NotificationException e) {
            logger.error("Failed to fetch templates: {}", e.getMessage());
            throw e;
        }
    }
} 