package com.example.notificationservice.integration;

import com.example.notificationservice.config.MultiTenantJpaConfig;
import com.example.notificationservice.config.TestDataSourceConfig;
import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.service.TenantService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({MultiTenantJpaConfig.class, TestDataSourceConfig.class})
class TenantServiceIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void shouldCreateTenantAndSchema() {
        // Given
        TenantOnboardingRequest request = new TenantOnboardingRequest();
        request.setTenantIdentifier("test_tenant");
        request.setName("Test Tenant");

        // When
        TenantOnboardingResponse response = tenantService.onboardNewTenant(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTenantIdentifier()).isEqualTo("test_tenant");
        assertThat(response.getName()).isEqualTo("Test Tenant");
        assertThat(response.getSchemaName()).isEqualTo("tenant_test_tenant");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");

        // Verify tenant exists in database
        Query tenantQuery = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM tenants WHERE tenant_identifier = :identifier");
        tenantQuery.setParameter("identifier", "test_tenant");
        Long tenantCount = ((Number) tenantQuery.getSingleResult()).longValue();
        assertThat(tenantCount).isEqualTo(1);

        // Verify schema exists
        Query schemaQuery = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = :schemaName");
        schemaQuery.setParameter("schemaName", "tenant_test_tenant");
        Long schemaCount = ((Number) schemaQuery.getSingleResult()).longValue();
        assertThat(schemaCount).isEqualTo(1);

        // Verify schema has required tables
        Query tableQuery = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = :schemaName");
        tableQuery.setParameter("schemaName", "tenant_test_tenant");
        Long tableCount = ((Number) tableQuery.getSingleResult()).longValue();
        assertThat(tableCount).isGreaterThan(0);
    }
} 