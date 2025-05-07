# Multi-Tenancy Architecture

## Overview
The notification service supports two types of multi-tenancy approaches:
1. Schema-Based (Same Database, Different Schemas)
2. Table-Based (Same Database, Different Tables)

## Database Design

### 1. Schema-Based Multi-tenancy
```
Database: notification_service
├── Schema: public (Default area)
│   └── tenants (Common table for all tenants)
│       ├── id (PK)
│       ├── name
│       ├── schema_name
│       ├── database_url
│       ├── admin_email
│       ├── admin_phone
│       ├── company_name
│       ├── active
│       ├── created_at
│       └── updated_at
│
├── Schema: tenant1 (Tenant-specific area)
│   ├── users
│   ├── notifications
│   ├── notification_templates
│   └── device_registrations
│
├── Schema: tenant2 (Tenant-specific area)
│   ├── users
│   ├── notifications
│   ├── notification_templates
│   └── device_registrations
```

### 2. Table-Based Multi-tenancy
```
Database: notification_service
├── Schema: public (Default area)
│   ├── tenants (Common table for all tenants)
│   │   ├── id (PK)
│   │   ├── name
│   │   ├── schema_name
│   │   ├── database_url
│   │   ├── admin_email
│   │   ├── admin_phone
│   │   ├── company_name
│   │   ├── active
│   │   ├── created_at
│   │   └── updated_at
│   │
│   ├── users_tenant1 (Tenant-specific tables)
│   ├── notifications_tenant1
│   ├── notification_templates_tenant1
│   ├── device_registrations_tenant1
│   │
│   ├── users_tenant2 (Tenant-specific tables)
│   ├── notifications_tenant2
│   ├── notification_templates_tenant2
│   └── device_registrations_tenant2
```

## Key Concepts

### Public Schema
- Default schema in PostgreSQL
- Contains shared/common tables
- Used for tenant management and configuration

### Shared Tables
- Tables that contain data common to all tenants
- Currently only the `tenants` table is shared
- Used for tenant identification and management

### Tenant-Specific Data
- Each tenant's data is completely isolated
- Either in separate schemas or with tenant-specific table names
- No cross-tenant data access

## Configuration

### application.yml
```yaml
multi-tenant:
  strategy: SCHEMA  # or TABLE
  default-tenant: default
```

### Switching Between Approaches
1. Schema-Based:
   - Set `multi-tenant.strategy: SCHEMA`
   - Each tenant gets their own schema
   - Better isolation and security

2. Table-Based:
   - Set `multi-tenant.strategy: TABLE`
   - All tables in same schema with tenant-specific names
   - Simpler implementation

## Implementation Details

### Schema-Based
- Uses `TenantPhysicalNamingStrategy`
- Each tenant has their own schema
- Better for larger tenants
- More complex to implement

### Table-Based
- Uses `TenantTableNamingStrategy`
- Tables have tenant-specific names
- Better for smaller tenants
- Simpler to implement

## Usage

```java
// Set tenant context before any database operation
TenantContext.setCurrentTenant("tenant1");

// JPA will automatically use:
// - Schema-based: tenant1 schema
// - Table-based: tables with _tenant1 suffix
```

## Security Considerations
- Each tenant's data is completely isolated
- No risk of data leakage between tenants
- Tenant context is thread-local
- Safe for concurrent requests 