package com.example.demologin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PermissionRequest {
    @NotBlank(message = "Permission name must not be blank")
    private String name;
    public AdminActionRequest adminAction;
}
