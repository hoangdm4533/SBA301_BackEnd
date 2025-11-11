package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.user.AdminUpdateUserRequest;
import com.example.demologin.dto.request.user.CreateUserRequest;
import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.service.UserService;
import com.example.demologin.utils.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users (admin only) & user self profile")

public class UserController {

    private final UserService userService;
    private final AccountUtils accountUtils;

    // 🔹 Admin: get all users (paginated)
    @GetMapping
    @ApiResponse(message = "Users retrieved successfully")
    @Operation(summary = "Get all users (paginated)", description = "Retrieve all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PageResponse
    public ResponseEntity<ResponseObject> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MemberResponse> data = userService.getAllUsers(page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Users retrieved successfully",
                data
        ));
    }

    // 🔹 Self: get my profile
    @GetMapping("/me")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Current user profile retrieved successfully")
    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> getCurrentUserProfile() {
        User currentUser = accountUtils.getCurrentUser();
        UserResponse data = UserResponse.toUserResponse(currentUser);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Current user profile retrieved successfully",
                data
        ));
    }

    // 🔹 Admin: get user by id
    @GetMapping("/{id}")
    @ApiResponse(message = "User retrieved successfully")
    @Operation(summary = "Get user by ID", description = "Admin only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> get(@PathVariable final Long id) {
        var data = userService.getById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "User retrieved successfully",
                data
        ));
    }

    // 🔹 Admin: create new user
    @PostMapping
    @ApiResponse(message = "User created successfully")
    @Operation(summary = "Create user", description = "Admin only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody final CreateUserRequest req) {
        var data = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(HttpStatus.CREATED.value(), "User created successfully", data));
    }

    // 🔹 Self: update my own profile
    @PutMapping("/me")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Profile updated successfully")
    @Operation(summary = "Update my profile",
            description = "User updates own profile (no roles/status/locked fields)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> updateMyProfile(@Valid @RequestBody final UpdateUserRequest req) {
        Long currentUserId = accountUtils.getCurrentUser().getUserId();
        var data = userService.updateSelf(currentUserId, req);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Profile updated successfully",
                data
        ));
    }

    // 🔹 Admin: update user
    @PutMapping("/{id}")
    @ApiResponse(message = "User updated successfully")
    @Operation(summary = "Admin update user", description = "Admin only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(@PathVariable final Long id,
                                                 @Valid @RequestBody final AdminUpdateUserRequest req) {
        var data = userService.updateAdmin(id, req);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "User updated successfully",
                data
        ));
    }

    // 🔹 Admin: delete user
    @DeleteMapping("/{id}")
    @ApiResponse(message = "User deleted successfully")
    @Operation(summary = "Delete user", description = "Admin only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable final Long id) {
        userService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "User deleted successfully",
                Map.of("id", id)
        ));
    }
}
