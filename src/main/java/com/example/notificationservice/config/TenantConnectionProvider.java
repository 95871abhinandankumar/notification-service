package com.example.notificationservice.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantConnectionProvider implements MultiTenantConnectionProvider {

    @Autowired
    private DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public Connection getConnection(String schemaName) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            if (schemaName != null) {
                log.debug("Setting search_path to schema: {}", schemaName);
                connection.createStatement().execute(String.format("SET search_path TO %s", schemaName));
            }
        } catch (SQLException e) {
            log.error("Could not alter search_path to {}", schemaName, e);
            throw new SQLException("Could not alter search_path to " + schemaName, e);
        }
        return connection;
    }

    @Override
    public void releaseConnection(String schemaName, Connection connection) throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                // Always reset to public schema when releasing connection
                log.debug("Resetting search_path to public schema");
                connection.createStatement().execute("SET search_path TO public");
                connection.close();
            }
        } catch (SQLException e) {
            log.warn("Could not reset search_path", e);
            throw e;
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
} 