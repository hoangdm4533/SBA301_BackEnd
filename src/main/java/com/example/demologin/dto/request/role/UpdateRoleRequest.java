package com.example.demologin.dto.request.role;

import com.example.demologin.dto.request.AdminActionRequest;
import jakarta.validation.constraints.NotBlank;

public class UpdateRoleRequest {
    @NotBlank(message = "Role name must not be blank")
    public String name;
    public AdminActionRequest adminAction;
}