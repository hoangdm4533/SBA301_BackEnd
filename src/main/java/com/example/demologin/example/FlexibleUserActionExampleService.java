package com.example.demologin.example;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.BaseActionRequest;
import com.example.demologin.enums.UserActionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * Enhanced example demonstrating flexible UserAction annotation with simplified auto-inject
 */
@Service
public class FlexibleUserActionExampleService {
    
    /**
     * Case 1: Auto-inject reason when requiresReason = true
     * Framework will automatically set reason in request if not provided
     */
    @UserAction(actionType = UserActionType.CREATE, requiresReason = true)
    public void createRoleWithAutoReason(CreateRoleFlexRequest request) {
        // If request.reason is null/empty, framework will auto-inject:
        // "Auto-generated: create role operation"
        System.out.println("Creating role with reason: " + request.getReason());
    }
    
    /**
     * Case 2: Require reason with auto-inject if missing
     * Will auto-inject if reason not provided
     */
    @UserAction(actionType = UserActionType.DELETE, requiresReason = true)
    public void deleteRoleWithAutoReason(Long roleId, DeleteRoleFlexRequest request) {
        // Framework will auto-inject reason if not provided
        System.out.println("Deleting role with reason: " + request.getReason());
    }
    
    /**
     * Case 3: No reason required - framework just logs the action
     */
    @UserAction(actionType = UserActionType.UPDATE)
    public void updateRoleNoReason(Long roleId, UpdateRoleFlexRequest request) {
        // No reason required, framework just logs the action
        System.out.println("Updating role: " + roleId);
    }
    
    /**
     * Case 4: No reason required, no auto-inject
     */
    @UserAction(actionType = UserActionType.VIEW)
    public void viewRole(Long roleId) {
        // No reason needed, framework just logs the action
        System.out.println("Viewing role: " + roleId);
    }
    
    /**
     * Case 5: Smart reason extraction and auto-inject
     * Framework will extract reason from BaseActionRequest or auto-inject if missing
     */
    @UserAction(actionType = UserActionType.EXPORT, requiresReason = true)
    public void exportRoles(String format, ExportOptionsRequest options) {
        // Framework will check:
        // 1. options.getReason() (if BaseActionRequest)
        // 2. Auto-inject if none found: "Auto-generated: export role operation"
        System.out.println("Exporting roles in " + format + " with reason: " + options.getReason());
    }
    
    // Example DTOs extending BaseActionRequest
    
    @Getter @Setter
    public static class CreateRoleFlexRequest extends BaseActionRequest {
        private String roleName;
        private String description;
        // reason inherited from BaseActionRequest
    }
    
    @Getter @Setter 
    public static class DeleteRoleFlexRequest extends BaseActionRequest {
        private boolean confirmed = false;
        // reason inherited from BaseActionRequest - REQUIRED for delete
    }
    
    @Getter @Setter
    public static class UpdateRoleFlexRequest extends BaseActionRequest {
        private String roleName;
        private String newDescription;
        // reason inherited from BaseActionRequest - optional but auto-injected
    }
    
    @Getter @Setter
    public static class ExportOptionsRequest extends BaseActionRequest {
        private boolean includePermissions = true;
        private String dateFormat = "yyyy-MM-dd";
        // reason inherited from BaseActionRequest
    }
}

/*
Usage Examples:

1. Client sends request WITH reason:
POST /api/roles
{
  "roleName": "NEW_ROLE",
  "reason": "Creating role for new department"
}
→ Framework uses provided reason

2. Client sends request WITHOUT reason (requiresReason = true):
POST /api/roles  
{
  "roleName": "NEW_ROLE"
}
→ Framework auto-injects: "Auto-generated: create role operation"

3. Client sends request for non-required reason action:
GET /api/roles/123
→ Framework just logs the action without requiring reason

4. Framework intelligence:
- Auto-detects targetType from method name (createRole → ROLE)
- Auto-extracts targetId from Long parameters
- Auto-extracts targetName from returned/fetched entities
- Auto-extracts reason from BaseActionRequest or auto-injects if requiresReason = true
- Smart context-aware auto-injection

Benefits:
✅ Simplified annotation (only requiresReason = true/false)
✅ Automatic reason injection when needed
✅ Intelligent parameter detection
✅ Better error messages with context
✅ Backward compatible with existing code
✅ Enterprise-ready logging with full audit trail
✅ No need to declare reason fields in DTOs explicitly
*/
