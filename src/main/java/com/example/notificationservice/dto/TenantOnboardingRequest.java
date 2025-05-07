package com.example.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantOnboardingRequest {
    @NotBlank(message = "Tenant identifier is required")
    @Size(min = 3, max = 50, message = "Tenant identifier must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Tenant identifier can only contain lowercase letters, numbers, and underscores")
    private String tenantIdentifier;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
} 