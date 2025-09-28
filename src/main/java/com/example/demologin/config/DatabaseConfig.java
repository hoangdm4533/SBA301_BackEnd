package com.example.demologin.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    @Transactional
    public void configureDatabaseSettings() {
        try {
            logger.info("Configuring MySQL database settings...");

            // Try to disable primary key requirement (might not work without SUPER privilege)
            try {
                jdbcTemplate.execute("SET GLOBAL sql_require_primary_key = 0");
                logger.info("Successfully disabled global primary key requirement");
            } catch (Exception e) {
                logger.warn("Could not set global sql_require_primary_key (requires SUPER privilege): {}", e.getMessage());
            }

            // Set session-level settings (these should work with normal privileges)
            jdbcTemplate.execute("SET SESSION sql_require_primary_key = 0");
            jdbcTemplate.execute("SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO'");
            jdbcTemplate.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");

            logger.info("Database session settings configured successfully");

        } catch (Exception e) {
            logger.error("Error configuring database settings: {}", e.getMessage());
            // Don't throw exception, let the application continue
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    @Transactional
    public void ensureJunctionTablesExist() {
        try {
            logger.info("Ensuring junction tables exist with proper structure...");

            // Wait a bit for Hibernate to create the main tables first
            Thread.sleep(2000);

            // Check if tables exist and create them if needed
            createUserRolesTable();
            createRolePermissionTable();

            logger.info("Junction tables verification completed");

        } catch (Exception e) {
            logger.error("Error ensuring junction tables exist: {}", e.getMessage());
            // Don't throw exception, let the application continue
        }
    }

    private void createUserRolesTable() {
        try {
            // Check if user_roles table exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'user_roles'",
                Integer.class
            );

            if (count != null && count == 0) {
                logger.info("Creating user_roles junction table...");
                jdbcTemplate.execute("""
                    CREATE TABLE user_roles (
                        user_id BIGINT NOT NULL,
                        role_id BIGINT NOT NULL,
                        PRIMARY KEY (user_id, role_id),
                        INDEX idx_user_roles_user_id (user_id),
                        INDEX idx_user_roles_role_id (role_id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
                logger.info("user_roles table created successfully");
            } else {
                logger.info("user_roles table already exists");
            }
        } catch (Exception e) {
            logger.warn("Could not create user_roles table: {}", e.getMessage());
        }
    }

    private void createRolePermissionTable() {
        try {
            // Check if role_permission table exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'role_permission'",
                Integer.class
            );

            if (count != null && count == 0) {
                logger.info("Creating role_permission junction table...");
                jdbcTemplate.execute("""
                    CREATE TABLE role_permission (
                        role_id BIGINT NOT NULL,
                        permission_id BIGINT NOT NULL,
                        PRIMARY KEY (role_id, permission_id),
                        INDEX idx_role_permission_role_id (role_id),
                        INDEX idx_role_permission_permission_id (permission_id)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
                logger.info("role_permission table created successfully");
            } else {
                logger.info("role_permission table already exists");
            }
        } catch (Exception e) {
            logger.warn("Could not create role_permission table: {}", e.getMessage());
        }
    }
}
