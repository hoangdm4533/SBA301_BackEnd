package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/permissions")
@Tag(name = "Permission Management", description = "APIs for managing system permissions")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @ApiResponse(message = "Permissions retrieved successfully")
    @Operation(summary = "Get all permissions",
            description = "Retrieve all system permissions")
    public ResponseEntity<ResponseObject> getAll() {
        final var data = permissionService.getAll();
        return ResponseEntity.ok(
                new ResponseObject(HttpStatus.OK.value(), "Permissions retrieved successfully", data)
        );
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Permission updated successfully")
    @Operation(summary = "Update permission",
            description = "Update permission name and description")
    public ResponseEntity<ResponseObject> update(
            @Parameter(description = "Permission ID") @PathVariable final Long id,
            @Valid @RequestBody final PermissionRequest req
    ) {
        final var data = permissionService.updatePermissionName(id, req);
        return ResponseEntity.ok(
                new ResponseObject(HttpStatus.OK.value(), "Permission updated successfully", data)
        );
    }
}
