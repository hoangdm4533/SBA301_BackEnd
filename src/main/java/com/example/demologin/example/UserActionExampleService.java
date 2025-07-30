package com.example.demologin.example;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import org.springframework.stereotype.Service;

/**
 * Example demonstrating how to use the new @UserAction annotation
 * This replaces the old @AdminAction annotation with a more flexible approach
 */
@Service
public class UserActionExampleService {
    
    /**
     * Simple action - framework automatically detects target type, id, and name
     */
    @UserAction(actionType = UserActionType.CREATE, description = "Create new user account")
    public void createUser(String username, String email) {
        // Implementation here
        // Framework will:
        // - Auto-detect targetType as "USER" (from method name)
        // - Auto-extract targetId from parameters or return value
        // - Auto-extract targetName from entity name field
        // - Log user info, IP, timestamp automatically
    }
    
    /**
     * Action with explicit target type
     */
    @UserAction(actionType = UserActionType.UPDATE, targetType = "ROLE", description = "Update role permissions")
    public void updateRolePermissions(Long roleId, String[] permissions) {
        // Implementation here
        // Framework will use "ROLE" as targetType
        // Auto-extract roleId as targetId
        // Auto-extract role name as targetName
    }
    
    /**
     * Action requiring reason
     */
    @UserAction(actionType = UserActionType.DELETE, targetType = "USER", requiresReason = true, description = "Delete user account")
    public void deleteUser(Long userId, String reason) {
        // Implementation here
        // Framework will require 'reason' parameter to be non-empty
        // If reason is missing or empty, will throw RuntimeException
    }
    
    /**
     * Action with minimal annotation - framework does all the work
     */
    @UserAction(actionType = UserActionType.VIEW)
    public void viewUserProfile(Long userId) {
        // Implementation here
        // Framework will:
        // - Auto-detect targetType = "USER" (from method name)
        // - Auto-extract targetId = userId
        // - Auto-generate description = "VIEW user: [username]"
        // - Auto-extract all other info
    }
    
    /**
     * Action with custom description
     */
    @UserAction(actionType = UserActionType.EXPORT, description = "Export user data to CSV")
    public void exportUsers(String format) {
        // Implementation here
        // targetType, targetId, targetName will be auto-detected or remain null if not applicable
    }
    
    /**
     * Works with any role - not just admin
     */
    @UserAction(actionType = UserActionType.UPDATE, targetType = "PROFILE", description = "Update personal profile")
    public void updateProfile(Long userId, String fullName, String email) {
        // Implementation here
        // Any user with appropriate permissions can perform this action
        // Framework logs their actual role(s) automatically
    }
}

/*
Key improvements over AdminAction:

1. Auto-detection:
   - targetType: Auto-detected from method name, parameter types, or class name
   - targetId: Auto-extracted from Long/Integer parameters or entity ID field
   - targetName: Auto-extracted from entity name/username/title fields
   - username, roleName, IP, userAgent: Auto-extracted from current user context

2. Flexible for all roles:
   - Not limited to "admin" actions
   - Works with any user role
   - Dynamic permission system compatible

3. Simplified annotation:
   - Only actionType is required
   - All other fields optional with smart defaults
   - Less boilerplate code

4. Enhanced logging:
   - More detailed information captured
   - Better change tracking
   - IP address and user agent logging
   - Multiple role support

5. Better performance:
   - Single aspect handles all logic
   - Efficient entity fetching
   - Smart field comparison

Usage examples:
- @UserAction(actionType = UserActionType.CREATE) - Minimal
- @UserAction(actionType = UserActionType.DELETE, requiresReason = true) - With reason
- @UserAction(actionType = UserActionType.UPDATE, targetType = "CUSTOM") - With explicit target
- @UserAction(actionType = UserActionType.VIEW, description = "Custom description") - With description
*/
