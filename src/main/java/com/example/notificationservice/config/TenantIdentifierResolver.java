package com.example.notificationservice.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.example.notificationservice.exception.TenantNotFoundException;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    private static final Logger logger = LoggerFactory.getLogger(TenantIdentifierResolver.class);

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        
        // Check if this is a tenant management operation
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String path = request.getRequestURI();
            // All tenant management endpoints should use public schema
            if (path.startsWith("/api/v1/tenants")) {
                logger.debug("Tenant management operation detected, using public schema");
                return "public";
            }
        }

        // For non-tenant management operations, require tenant ID
        if (tenantId == null) {
            // Only throw exception if we're in a request context
            if (attributes != null) {
                logger.error("No tenant identifier found for non-tenant management operation");
                throw new TenantNotFoundException("Tenant identifier is required for this operation");
            }
            // During initialization or non-request context, use public schema
            logger.debug("No request context, using public schema");
            return "public";
        }

        logger.debug("Using tenant identifier from context: {}", tenantId);
        return "tenant_" + tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
} 