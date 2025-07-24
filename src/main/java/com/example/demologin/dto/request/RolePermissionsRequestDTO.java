package com.example.demologin.dto.request;

import java.util.Set;

public class RolePermissionsRequestDTO {
    public Set<Long> permissionIds;
    public AdminActionRequest adminAction;
}
