package com.example.notificationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {
    private Long id;
    private String tenantIdentifier;
    private String name;
    private String schemaName;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 