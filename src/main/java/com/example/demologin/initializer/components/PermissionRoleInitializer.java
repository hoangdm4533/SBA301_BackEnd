package com.example.demologin.initializer.components;

import com.example.demologin.entity.Permission;
import com.example.demologin.entity.Role;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission and Role Initializer
 * 
 * Responsible for creating all system permissions and roles.
 * This must run before user initialization since users depend on roles.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionRoleInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void initializePermissionsAndRoles() {
        log.info("🔑 Initializing system permissions and roles...");
        
        if (permissionRepository.count() > 0) {
            log.info("ℹ️ Permissions already exist, skipping initialization");
            return;
        }

        createPermissions();
        createRoles();
        
        log.info("✅ Successfully initialized {} permissions and {} roles", 
                permissionRepository.count(), roleRepository.count());
    }

    private void createPermissions() {
        log.debug("📋 Creating system permissions...");
        
        // Session Management Permissions  
        permissionRepository.save(new Permission("USER_TOKEN_MANAGEMENT", "Quản lý token của user"));
        permissionRepository.save(new Permission("ADMIN_USER_MANAGEMENT", "Quản lý user (Admin)"));
        
        // Token Management Permissions
        permissionRepository.save(new Permission("TOKEN_INVALIDATE_OWN", "Hủy token của bản thân"));
        permissionRepository.save(new Permission("TOKEN_INVALIDATE_ANY", "Hủy token của bất kỳ user nào"));
        permissionRepository.save(new Permission("TOKEN_VIEW_OWN", "Xem token version của bản thân"));
        permissionRepository.save(new Permission("TOKEN_VIEW_ANY", "Xem token version của bất kỳ user nào"));
        permissionRepository.save(new Permission("TOKEN_RESET", "Reset token version"));
        permissionRepository.save(new Permission("TOKEN_VALIDATE", "Validate token version"));
        
        // User Action Log Permissions
        permissionRepository.save(new Permission("LOG_VIEW_ALL", "Xem tất cả action logs"));
        permissionRepository.save(new Permission("LOG_VIEW_USER", "Xem action logs của user cụ thể"));
        permissionRepository.save(new Permission("LOG_SEARCH", "Tìm kiếm action logs"));
        permissionRepository.save(new Permission("LOG_DELETE", "Xóa action logs"));
        
        // Role & Permission Management
        permissionRepository.save(new Permission("ROLE_VIEW", "Xem vai trò"));
        permissionRepository.save(new Permission("ROLE_CREATE", "Tạo vai trò"));
        permissionRepository.save(new Permission("ROLE_UPDATE", "Cập nhật vai trò"));
        permissionRepository.save(new Permission("ROLE_DELETE", "Xóa vai trò"));
        permissionRepository.save(new Permission("ROLE_UPDATE_PERMISSIONS", "Gán quyền cho vai trò"));
        permissionRepository.save(new Permission("PERMISSION_VIEW", "Xem quyền"));
        permissionRepository.save(new Permission("PERMISSION_UPDATE", "Cập nhật quyền"));
        
        // Admin Action Log
        permissionRepository.save(new Permission("ADMIN_ACTION_LOG_VIEW", "Xem admin action log"));
        
        // User Activity Log Permissions
        permissionRepository.save(new Permission("LOG_VIEW_ACTIVITY", "Xem user activity logs"));
        permissionRepository.save(new Permission("LOG_SEARCH_ACTIVITY", "Tìm kiếm user activity logs"));
        permissionRepository.save(new Permission("LOG_VIEW_USER_ACTIVITY", "Xem activity logs của user cụ thể"));
        permissionRepository.save(new Permission("LOG_EXPORT_ACTIVITY", "Export user activity logs"));
        permissionRepository.save(new Permission("LOG_VIEW_STATS", "Xem thống kê activity"));
        permissionRepository.save(new Permission("LOG_DELETE_ACTIVITY", "Xóa user activity logs"));

        // Security Management Permissions
        permissionRepository.save(new Permission("ADMIN_SECURITY_MANAGEMENT", "Quản lý bảo mật tài khoản (unlock, xem login attempts)"));
        
        // User Personal Data Permissions
        permissionRepository.save(new Permission("USER_VIEW_OWN_LOGIN_HISTORY", "Xem lịch sử đăng nhập của bản thân"));
        
        log.debug("✅ Created {} permissions", permissionRepository.count());
    }

    private void createRoles() {
        log.debug("👑 Creating system roles...");
        
        // Get all permissions for role assignment
        Permission p1 = permissionRepository.findByCode("USER_TOKEN_MANAGEMENT").orElseThrow();
        Permission p2 = permissionRepository.findByCode("ADMIN_USER_MANAGEMENT").orElseThrow();
        Permission p3 = permissionRepository.findByCode("TOKEN_INVALIDATE_OWN").orElseThrow();
        Permission p4 = permissionRepository.findByCode("TOKEN_INVALIDATE_ANY").orElseThrow();
        Permission p5 = permissionRepository.findByCode("TOKEN_VIEW_OWN").orElseThrow();
        Permission p6 = permissionRepository.findByCode("TOKEN_VIEW_ANY").orElseThrow();
        Permission p7 = permissionRepository.findByCode("TOKEN_RESET").orElseThrow();
        Permission p8 = permissionRepository.findByCode("TOKEN_VALIDATE").orElseThrow();
        Permission p9 = permissionRepository.findByCode("LOG_VIEW_ALL").orElseThrow();
        Permission p10 = permissionRepository.findByCode("LOG_VIEW_USER").orElseThrow();
        Permission p11 = permissionRepository.findByCode("LOG_SEARCH").orElseThrow();
        Permission p12 = permissionRepository.findByCode("LOG_DELETE").orElseThrow();
        Permission p13 = permissionRepository.findByCode("ROLE_VIEW").orElseThrow();
        Permission p14 = permissionRepository.findByCode("ROLE_CREATE").orElseThrow();
        Permission p15 = permissionRepository.findByCode("ROLE_UPDATE").orElseThrow();
        Permission p16 = permissionRepository.findByCode("ROLE_DELETE").orElseThrow();
        Permission p17 = permissionRepository.findByCode("ROLE_UPDATE_PERMISSIONS").orElseThrow();
        Permission p18 = permissionRepository.findByCode("PERMISSION_VIEW").orElseThrow();
        Permission p19 = permissionRepository.findByCode("PERMISSION_UPDATE").orElseThrow();
        Permission p20 = permissionRepository.findByCode("ADMIN_ACTION_LOG_VIEW").orElseThrow();
        Permission p21 = permissionRepository.findByCode("LOG_VIEW_ACTIVITY").orElseThrow();
        Permission p22 = permissionRepository.findByCode("LOG_SEARCH_ACTIVITY").orElseThrow();
        Permission p23 = permissionRepository.findByCode("LOG_VIEW_USER_ACTIVITY").orElseThrow();
        Permission p24 = permissionRepository.findByCode("LOG_EXPORT_ACTIVITY").orElseThrow();
        Permission p25 = permissionRepository.findByCode("LOG_VIEW_STATS").orElseThrow();
        Permission p26 = permissionRepository.findByCode("LOG_DELETE_ACTIVITY").orElseThrow();
        Permission p27 = permissionRepository.findByCode("ADMIN_SECURITY_MANAGEMENT").orElseThrow();
        Permission p28 = permissionRepository.findByCode("USER_VIEW_OWN_LOGIN_HISTORY").orElseThrow();

        // Admin permissions (full access)
        Set<Permission> adminPerms = new HashSet<>();
        adminPerms.add(p1); adminPerms.add(p2); adminPerms.add(p3); adminPerms.add(p4);
        adminPerms.add(p5); adminPerms.add(p6); adminPerms.add(p7); adminPerms.add(p8);
        adminPerms.add(p9); adminPerms.add(p10); adminPerms.add(p11); adminPerms.add(p12);
        adminPerms.add(p13); adminPerms.add(p14); adminPerms.add(p15); adminPerms.add(p16);
        adminPerms.add(p17); adminPerms.add(p18); adminPerms.add(p19); adminPerms.add(p20);
        adminPerms.add(p21); adminPerms.add(p22); adminPerms.add(p23); adminPerms.add(p24);
        adminPerms.add(p25); adminPerms.add(p26); adminPerms.add(p27); adminPerms.add(p28);
        
        // Member permissions (limited access)
        Set<Permission> memberPerms = new HashSet<>();
        memberPerms.add(p1); // USER_TOKEN_MANAGEMENT
        memberPerms.add(p3); // TOKEN_INVALIDATE_OWN  
        memberPerms.add(p5); // TOKEN_VIEW_OWN
        memberPerms.add(p8); // TOKEN_VALIDATE
        memberPerms.add(p28); // USER_VIEW_OWN_LOGIN_HISTORY

        roleRepository.save(new Role("ADMIN", adminPerms));
        roleRepository.save(new Role("MEMBER", memberPerms));
        
        log.debug("✅ Created {} roles", roleRepository.count());
    }
}
