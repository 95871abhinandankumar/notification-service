package com.example.notificationservice.repository;

import com.example.notificationservice.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByTenantIdentifier(String tenantIdentifier);
    Optional<Tenant> findBySchemaName(String schemaName);
    boolean existsByTenantIdentifier(String tenantIdentifier);
    boolean existsBySchemaName(String schemaName);
} 