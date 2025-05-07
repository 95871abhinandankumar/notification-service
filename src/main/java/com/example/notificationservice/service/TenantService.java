package com.example.notificationservice.service;

import com.example.notificationservice.dto.TenantDTO;
import com.example.notificationservice.model.Tenant;
import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant createTenant(TenantDTO tenantDTO);
    Optional<Tenant> getTenantById(Long id);
    Optional<Tenant> getTenantByIdentifier(String tenantIdentifier);
    List<Tenant> getAllTenants();
    Tenant updateTenant(Long id, TenantDTO tenantDTO);
    void deleteTenant(Long id);
    void activateTenant(Long id);
    void deactivateTenant(Long id);
    TenantOnboardingResponse onboardNewTenant(TenantOnboardingRequest request);
    TenantOnboardingResponse getTenant(String tenantIdentifier);
    void activateTenant(String tenantIdentifier);
    void deactivateTenant(String tenantIdentifier);
    boolean verifySchemaExists(String schemaName);
    boolean verifyTenantExists(String tenantIdentifier);
} 