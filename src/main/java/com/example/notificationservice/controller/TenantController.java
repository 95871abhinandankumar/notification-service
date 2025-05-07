package com.example.notificationservice.controller;

import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
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
} 