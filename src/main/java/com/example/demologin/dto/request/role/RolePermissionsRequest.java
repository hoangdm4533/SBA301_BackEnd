package com.example.demologin.dto.request.role;

import com.example.demologin.dto.request.AdminActionRequest;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class RolePermissionsRequest {
    @NotEmpty(message = "Permission IDs must not be empty")
    public Set<Long> permissionIds;
    public AdminActionRequest adminAction;
}
