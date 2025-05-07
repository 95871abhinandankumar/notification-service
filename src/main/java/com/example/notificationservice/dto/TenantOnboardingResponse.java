package com.example.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantOnboardingResponse {
    private String tenantIdentifier;
    private String name;
    private String schemaName;
    private String status;
    private LocalDateTime createdAt;
} 