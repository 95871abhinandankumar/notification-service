package com.example.notificationservice.controller;

import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = "APIs for managing tenants and their schemas")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/onboard")
    public ResponseEntity<TenantOnboardingResponse> onboardTenant(
            @Valid @RequestBody TenantOnboardingRequest request) {
        log.info("Received tenant onboarding request for tenant: {}", request.getTenantIdentifier());
        
        TenantOnboardingResponse response = tenantService.onboardNewTenant(request);
        
        log.info("Successfully onboarded tenant: {}", response.getTenantIdentifier());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tenantIdentifier}")
    public ResponseEntity<TenantOnboardingResponse> getTenant(
            @PathVariable String tenantIdentifier) {
        log.info("Fetching tenant information for: {}", tenantIdentifier);
        
        TenantOnboardingResponse response = tenantService.getTenant(tenantIdentifier);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tenantIdentifier}/activate")
    public ResponseEntity<Void> activateTenant(
            @PathVariable String tenantIdentifier) {
        log.info("Activating tenant: {}", tenantIdentifier);
        
        tenantService.activateTenant(tenantIdentifier);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tenantIdentifier}/deactivate")
    public ResponseEntity<Void> deactivateTenant(
            @PathVariable String tenantIdentifier) {
        log.info("Deactivating tenant: {}", tenantIdentifier);
        
        tenantService.deactivateTenant(tenantIdentifier);
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recreate tenant schema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schema recreated successfully"),
        @ApiResponse(responseCode = "404", description = "Tenant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{tenantIdentifier}/recreate-schema")
    public ResponseEntity<Void> recreateTenantSchema(
            @Parameter(description = "Tenant identifier", required = true) 
            @PathVariable String tenantIdentifier) {
        try {
            log.info("Recreating schema for tenant: {}", tenantIdentifier);
            tenantService.recreateTenantSchema(tenantIdentifier);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to recreate schema: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<TenantOnboardingResponse>> getAllTenants() {
        log.info("Fetching all tenants");
        List<TenantOnboardingResponse> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }
} 