package com.example.notificationservice.service.impl;

import com.example.notificationservice.config.TenantConstants;
import com.example.notificationservice.dto.TenantDTO;
import com.example.notificationservice.dto.TenantOnboardingRequest;
import com.example.notificationservice.dto.TenantOnboardingResponse;
import com.example.notificationservice.exception.TenantAlreadyExistsException;
import com.example.notificationservice.exception.TenantNotFoundException;
import com.example.notificationservice.model.Tenant;
import com.example.notificationservice.repository.TenantRepository;
import com.example.notificationservice.service.TenantService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TenantOnboardingResponse onboardNewTenant(TenantOnboardingRequest request) {
        log.debug("Onboarding tenant with identifier: {}", request.getTenantIdentifier());

        // Validate tenant doesn't exist
        if (tenantRepository.existsByTenantIdentifier(request.getTenantIdentifier())) {
            throw new TenantAlreadyExistsException("Tenant with identifier " + request.getTenantIdentifier() + " already exists");
        }

        String schemaName = TenantConstants.SCHEMA_PREFIX + request.getTenantIdentifier();
        if (tenantRepository.existsBySchemaName(schemaName)) {
            throw new TenantAlreadyExistsException("Schema " + schemaName + " already exists");
        }

        try {
            // Create tenant record
            Tenant tenant = new Tenant();
            tenant.setTenantIdentifier(request.getTenantIdentifier());
            tenant.setName(request.getName());
            tenant.setSchemaName(schemaName);
            tenant.setActive(true);
            tenant.setCreatedAt(LocalDateTime.now());
            tenant.setUpdatedAt(LocalDateTime.now());

            tenant = tenantRepository.save(tenant);

            // Create and initialize schema
            createAndInitializeSchema(schemaName);

            // Prepare response
            TenantOnboardingResponse response = new TenantOnboardingResponse();
            response.setTenantIdentifier(tenant.getTenantIdentifier());
            response.setName(tenant.getName());
            response.setSchemaName(tenant.getSchemaName());
            response.setStatus(tenant.isActive() ? "ACTIVE" : "INACTIVE");
            response.setCreatedAt(tenant.getCreatedAt());

            log.debug("Successfully onboarded tenant: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Failed to onboard tenant: {}", request.getTenantIdentifier(), e);
            throw new RuntimeException("Failed to onboard tenant: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public TenantOnboardingResponse getTenant(String tenantIdentifier) {
        Tenant tenant = tenantRepository.findByTenantIdentifier(tenantIdentifier)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with identifier: " + tenantIdentifier));

        TenantOnboardingResponse response = new TenantOnboardingResponse();
        response.setTenantIdentifier(tenant.getTenantIdentifier());
        response.setName(tenant.getName());
        response.setSchemaName(tenant.getSchemaName());
        response.setStatus(tenant.isActive() ? "ACTIVE" : "INACTIVE");
        response.setCreatedAt(tenant.getCreatedAt());

        return response;
    }

    @Override
    @Transactional
    public void activateTenant(String tenantIdentifier) {
        Tenant tenant = tenantRepository.findByTenantIdentifier(tenantIdentifier)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with identifier: " + tenantIdentifier));

        tenant.setActive(true);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void deactivateTenant(String tenantIdentifier) {
        Tenant tenant = tenantRepository.findByTenantIdentifier(tenantIdentifier)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with identifier: " + tenantIdentifier));

        tenant.setActive(false);
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantRepository.save(tenant);
    }

    private void createAndInitializeSchema(String schemaName) {
        log.info("Creating schema: {}", schemaName);
        
        try {
            // Create schema
            entityManager.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + schemaName).executeUpdate();
            
            // Set search path to new schema
            entityManager.createNativeQuery("SET search_path TO " + schemaName).executeUpdate();
            
            // Execute the tenant schema SQL script
            String sqlScript = new String(getClass().getResourceAsStream("/db/tenant-schema.sql").readAllBytes());
            String[] statements = sqlScript.split(";");
            
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    try {
                        entityManager.createNativeQuery(statement.trim()).executeUpdate();
                    } catch (Exception e) {
                        log.error("Error executing SQL statement: {}", statement, e);
                        throw e;
                    }
                }
            }

            // Reset search path to public
            entityManager.createNativeQuery("SET search_path TO public").executeUpdate();
            
            log.info("Successfully created schema and tables for: {}", schemaName);
        } catch (IOException e) {
            log.error("Error reading tenant schema SQL file", e);
            throw new RuntimeException("Failed to read tenant schema SQL file", e);
        } catch (Exception e) {
            log.error("Error creating schema {}: {}", schemaName, e.getMessage(), e);
            throw new RuntimeException("Failed to create tenant schema: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Tenant createTenant(TenantDTO tenantDTO) {
        log.debug("Creating tenant with identifier: {}", tenantDTO.getTenantIdentifier());

        if (tenantRepository.existsByTenantIdentifier(tenantDTO.getTenantIdentifier())) {
            throw new TenantAlreadyExistsException("Tenant with identifier " + tenantDTO.getTenantIdentifier() + " already exists");
        }

        String schemaName = TenantConstants.SCHEMA_PREFIX + tenantDTO.getTenantIdentifier();
        if (tenantRepository.existsBySchemaName(schemaName)) {
            throw new TenantAlreadyExistsException("Schema " + schemaName + " already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setTenantIdentifier(tenantDTO.getTenantIdentifier());
        tenant.setName(tenantDTO.getName());
        tenant.setSchemaName(schemaName);
        tenant.setActive(true);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());

        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> getTenantByIdentifier(String tenantIdentifier) {
        return tenantRepository.findByTenantIdentifier(tenantIdentifier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantOnboardingResponse> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        return tenants.stream()
            .map(tenant -> {
                TenantOnboardingResponse response = new TenantOnboardingResponse();
                response.setTenantIdentifier(tenant.getTenantIdentifier());
                response.setName(tenant.getName());
                response.setSchemaName(tenant.getSchemaName());
                response.setStatus(tenant.isActive() ? "ACTIVE" : "INACTIVE");
                response.setCreatedAt(tenant.getCreatedAt());
                return response;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Tenant updateTenant(Long id, TenantDTO tenantDTO) {
        log.info("Updating tenant with id: {}", id);
        
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with id: " + id));

        if (!tenant.getTenantIdentifier().equals(tenantDTO.getTenantIdentifier()) &&
            tenantRepository.existsByTenantIdentifier(tenantDTO.getTenantIdentifier())) {
            throw new IllegalArgumentException("Tenant identifier already exists");
        }

        if (!tenant.getSchemaName().equals(tenantDTO.getSchemaName()) &&
            tenantRepository.existsBySchemaName(tenantDTO.getSchemaName())) {
            throw new IllegalArgumentException("Schema name already exists");
        }

        tenant.setTenantIdentifier(tenantDTO.getTenantIdentifier());
        tenant.setName(tenantDTO.getName());
        tenant.setSchemaName(tenantDTO.getSchemaName());

        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        log.info("Deleting tenant with id: {}", id);
        
        if (!tenantRepository.existsById(id)) {
            throw new EntityNotFoundException("Tenant not found with id: " + id);
        }
        
        tenantRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activateTenant(Long id) {
        log.info("Activating tenant with id: {}", id);
        
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with id: " + id));
        
        tenant.setActive(true);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public void deactivateTenant(Long id) {
        log.info("Deactivating tenant with id: {}", id);
        
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with id: " + id));
        
        tenant.setActive(false);
        tenantRepository.save(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifySchemaExists(String schemaName) {
        try {
            String sql = "SELECT schema_name FROM information_schema.schemata WHERE schema_name = :schemaName";
            String result = (String) entityManager.createNativeQuery(sql)
                .setParameter("schemaName", schemaName)
                .getSingleResult();
            return result != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyTenantExists(String tenantIdentifier) {
        return tenantRepository.findByTenantIdentifier(tenantIdentifier).isPresent();
    }

    @Override
    @Transactional
    public void recreateTenantSchema(String tenantIdentifier) {
        log.info("Recreating schema for tenant: {}", tenantIdentifier);
        
        Tenant tenant = tenantRepository.findByTenantIdentifier(tenantIdentifier)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found with identifier: " + tenantIdentifier));
        
        String schemaName = tenant.getSchemaName();
        
        try {
            // Drop existing schema
            entityManager.createNativeQuery("DROP SCHEMA IF EXISTS " + schemaName + " CASCADE").executeUpdate();
            
            // Create and initialize new schema
            createAndInitializeSchema(schemaName);
            
            log.info("Successfully recreated schema for tenant: {}", tenantIdentifier);
        } catch (Exception e) {
            log.error("Failed to recreate schema for tenant: {}", tenantIdentifier, e);
            throw new RuntimeException("Failed to recreate tenant schema: " + e.getMessage(), e);
        }
    }
} 