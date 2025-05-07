package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.exception.TenantAlreadyExistsException;
import com.example.notificationservice.exception.TenantNotFoundException;
import com.example.notificationservice.model.Tenant;
import com.example.notificationservice.repository.TenantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private TenantOnboardingRequest request;
    private Tenant tenant;

    @BeforeEach
    void setUp() {
        request = new TenantOnboardingRequest();
        request.setTenantIdentifier("test_tenant");
        request.setName("Test Tenant");

        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setTenantIdentifier("test_tenant");
        tenant.setName("Test Tenant");
        tenant.setSchemaName("tenant_test_tenant");
        tenant.setActive(true);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void verifyTenantAndSchemaExistence() {
        // Given
        ReflectionTestUtils.setField(tenantService, "entityManager", entityManager);
        
        // Mock schema existence check
        when(entityManager.createNativeQuery(eq("SELECT schema_name FROM information_schema.schemata WHERE schema_name = :schemaName")))
            .thenReturn(query);
        when(query.setParameter(eq("schemaName"), eq("tenant_test_tenant"))).thenReturn(query);
        when(query.getSingleResult()).thenReturn("tenant_test_tenant");

        // Mock tenant existence check
        when(tenantRepository.findByTenantIdentifier("test_tenant")).thenReturn(Optional.of(tenant));

        // When
        boolean schemaExists = tenantService.verifySchemaExists("tenant_test_tenant");
        boolean tenantExists = tenantService.verifyTenantExists("test_tenant");

        // Then
        assertTrue(schemaExists, "Schema should exist");
        assertTrue(tenantExists, "Tenant should exist");

        verify(entityManager).createNativeQuery("SELECT schema_name FROM information_schema.schemata WHERE schema_name = :schemaName");
        verify(query).setParameter("schemaName", "tenant_test_tenant");
        verify(tenantRepository).findByTenantIdentifier("test_tenant");
    }

    @Test
    void onboardNewTenant_Success() {
        // Given
        ReflectionTestUtils.setField(tenantService, "entityManager", entityManager);
        lenient().when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        lenient().when(query.executeUpdate()).thenReturn(1);

        when(tenantRepository.existsByTenantIdentifier(anyString())).thenReturn(false);
        when(tenantRepository.existsBySchemaName(anyString())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        // When
        TenantOnboardingResponse response = tenantService.onboardNewTenant(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getTenantIdentifier(), response.getTenantIdentifier());
        assertEquals(request.getName(), response.getName());
        assertEquals("tenant_test_tenant", response.getSchemaName());
        assertEquals("ACTIVE", response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(tenantRepository).existsByTenantIdentifier(request.getTenantIdentifier());
        verify(tenantRepository).existsBySchemaName("tenant_test_tenant");
        verify(tenantRepository).save(any(Tenant.class));
        verify(entityManager, atLeastOnce()).createNativeQuery(anyString());
    }

    @Test
    void onboardNewTenant_WhenTenantExists_ThrowsException() {
        // Given
        when(tenantRepository.existsByTenantIdentifier(anyString())).thenReturn(true);

        // When/Then
        assertThrows(TenantAlreadyExistsException.class,
                () -> tenantService.onboardNewTenant(request));

        verify(tenantRepository).existsByTenantIdentifier(request.getTenantIdentifier());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    void getTenant_Success() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.of(tenant));

        // When
        TenantOnboardingResponse response = tenantService.getTenant("test_tenant");

        // Then
        assertNotNull(response);
        assertEquals(tenant.getTenantIdentifier(), response.getTenantIdentifier());
        assertEquals(tenant.getName(), response.getName());
        assertEquals(tenant.getSchemaName(), response.getSchemaName());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(tenant.getCreatedAt(), response.getCreatedAt());

        verify(tenantRepository).findByTenantIdentifier("test_tenant");
    }

    @Test
    void getTenant_WhenNotFound_ThrowsException() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(TenantNotFoundException.class,
                () -> tenantService.getTenant("non_existent_tenant"));

        verify(tenantRepository).findByTenantIdentifier("non_existent_tenant");
    }

    @Test
    void activateTenant_Success() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        // When
        tenantService.activateTenant("test_tenant");

        // Then
        verify(tenantRepository).findByTenantIdentifier("test_tenant");
        verify(tenantRepository).save(tenant);
        assertTrue(tenant.isActive());
    }

    @Test
    void activateTenant_WhenNotFound_ThrowsException() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(TenantNotFoundException.class,
                () -> tenantService.activateTenant("non_existent_tenant"));

        verify(tenantRepository).findByTenantIdentifier("non_existent_tenant");
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    @Test
    void deactivateTenant_Success() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        // When
        tenantService.deactivateTenant("test_tenant");

        // Then
        verify(tenantRepository).findByTenantIdentifier("test_tenant");
        verify(tenantRepository).save(tenant);
        assertFalse(tenant.isActive());
    }

    @Test
    void deactivateTenant_WhenNotFound_ThrowsException() {
        // Given
        when(tenantRepository.findByTenantIdentifier(anyString())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(TenantNotFoundException.class,
                () -> tenantService.deactivateTenant("non_existent_tenant"));

        verify(tenantRepository).findByTenantIdentifier("non_existent_tenant");
        verify(tenantRepository, never()).save(any(Tenant.class));
    }
} 