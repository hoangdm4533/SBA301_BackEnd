package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PublicEndpoint;
import com.example.demologin.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/test")
public class test {

    private final RoleService roleService;

    @GetMapping("/get-all")
    @PublicEndpoint
    @ApiResponse(message = "Roles retrieved successfully")
    public Object getAll() {
       return roleService.getAll();

    }


}
