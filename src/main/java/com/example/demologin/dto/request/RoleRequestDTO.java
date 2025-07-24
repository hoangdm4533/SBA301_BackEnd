package com.example.demologin.dto.request;

import java.util.Set;

public class RoleRequestDTO {
    public String name;
    public Set<Long> permissionIds;
    public AdminActionRequest adminAction;
}
