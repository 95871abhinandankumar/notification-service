package com.example.notificationservice.controller;

import com.example.notificationservice.model.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User details", required = true) 
            @Valid @RequestBody User user) {
        try {
            logger.info("Creating new user with email: {}", user.getEmail());
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (NotificationException e) {
            logger.error("Failed to create user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID", required = true) 
            @PathVariable Long id) {
        try {
            logger.info("Fetching user with ID: {}", id);
            return userService.getUserById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NotificationException e) {
            logger.error("Failed to get user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "User email", required = true) 
            @PathVariable String email) {
        try {
            logger.info("Fetching user with email: {}", email);
            return userService.getUserByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NotificationException e) {
            logger.error("Failed to get user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get user by phone number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(
            @Parameter(description = "User phone number", required = true) 
            @PathVariable String phoneNumber) {
        try {
            logger.info("Fetching user with phone number: {}", phoneNumber);
            return userService.getUserByPhoneNumber(phoneNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (NotificationException e) {
            logger.error("Failed to get user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated user details", required = true) 
            @Valid @RequestBody User user) {
        try {
            logger.info("Updating user with ID: {}", id);
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (NotificationException e) {
            logger.error("Failed to update user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationResponse> deleteUser(
            @Parameter(description = "User ID", required = true) 
            @PathVariable Long id) {
        try {
            logger.info("Deleting user with ID: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok(new NotificationResponse(true, "User deleted successfully"));
        } catch (NotificationException e) {
            logger.error("Failed to delete user: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            logger.info("Fetching all users");
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (NotificationException e) {
            logger.error("Failed to get users: {}", e.getMessage());
            throw e;
        }
    }
} 