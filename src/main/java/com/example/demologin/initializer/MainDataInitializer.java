package com.example.demologin.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demologin.initializer.components.DefaultUserInitializer;
import com.example.demologin.initializer.components.PermissionRoleInitializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Data Initializer - Orchestrates all initialization processes
 * 
 * This class coordinates the execution of all data initialization components
 * in the correct order to ensure system integrity and proper dependencies.
 * 
 * Execution Order:
 * 1. PermissionRoleInitializer - Creates permissions and roles
 * 2. DefaultUserInitializer - Creates default users with assigned roles
 * 3. PlanDataInitializer - Creates subscription plans
 * 4. EducationDataInitializer - Creates grades, classes, and lesson plans
 * 5. ChapterDataInitializer - Creates chapters for lesson plans
 * 6. LevelDataInitializer - Creates exam levels
 * 7. ExamDataInitializer - Creates questions, options, and exams
 * 8. ExamTemplateDataInitializer - Creates exam templates with questions
 * 9. ExamQuestionDataInitializer - Links questions to exam templates
 * 10. Future initializers can be added here with proper ordering
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Ensure this runs first among all CommandLineRunners
public class MainDataInitializer implements CommandLineRunner {

    private final PermissionRoleInitializer permissionRoleInitializer;
    private final DefaultUserInitializer defaultUserInitializer;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting Main Data Initialization Process...");
        
        try {
            // Step 1: Initialize Permissions and Roles
            log.info("📋 Step 1: Initializing Permissions and Roles...");
            permissionRoleInitializer.initializePermissionsAndRoles();
            log.info("✅ Permissions and Roles initialization completed");
            
            // Step 2: Initialize Default Users
            log.info("👥 Step 2: Initializing Default Users...");
            defaultUserInitializer.initializeDefaultUsers();
            log.info("✅ Default Users initialization completed");
            
            // Future initialization steps can be added here
            // Example:
            // log.info("📊 Step 10: Initializing System Settings...");
            // systemSettingsInitializer.initializeSettings();
            
            log.info("🎉 Main Data Initialization Process completed successfully!");
            
        } catch (Exception e) {
            log.error("❌ Error during data initialization: {}", e.getMessage(), e);
            throw e; // Re-throw to prevent application startup with incomplete data
        }
    }
}
