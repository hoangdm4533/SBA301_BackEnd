package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.user.AdminUpdateUserRequest;
import com.example.demologin.dto.request.user.CreateUserRequest;
import com.example.demologin.dto.request.user.UpdateUserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.mapper.UserMapper;
import com.example.demologin.service.UserService;
import com.example.demologin.utils.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users (admin only)")
public class UserController {

    private final UserService userService;
    private final AccountUtils accountUtils;

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Users retrieved successfully")
    @Operation(summary = "Get all users (paginated)",
            description = "Retrieve paginated list of all users in the system (admin only)")
    public Object getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.getAllUsers(page, size);
    }

    @ApiResponse(message = "Users retrieved successfully")
    @GetMapping("/me")
    @AuthenticatedEndpoint
    @Operation(summary = "Get current user profile", description = "Retrieve profile of the authenticated user")
    public Object getCurrentUserProfile() {
        User currentUser = accountUtils.getCurrentUser();
        return  UserResponse.toUserResponse(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        var data = userService.getById(id);
        return ResponseEntity.ok(new ResponseObject(200, "User retrieved successfully", data));
    }

    @PostMapping
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody CreateUserRequest req) {
        var data = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "User created successfully", data));
    }

    @PutMapping("/me")
    @AuthenticatedEndpoint
    @Operation(summary = "Update my profile",
            description = "User updates own profile (no status/locked/verify/roles)")
    public ResponseEntity<ResponseObject> updateMyProfile(@Valid @RequestBody UpdateUserRequest req) {
        // Lưu ý: nếu User entity có getter là getId() thì đổi sang getId()
        Long currentUserId = accountUtils.getCurrentUser().getUserId();
        var data = userService.updateSelf(currentUserId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Profile updated successfully", data));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Admin update user", description = "Admin only")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id,
                                                 @Valid @RequestBody AdminUpdateUserRequest req) {
        var data = userService.updateAdmin(id, req);
        return ResponseEntity.ok(new ResponseObject(200, "User updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Admin only")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(
                new ResponseObject(
                        HttpStatus.OK.value(),
                        "User deleted successfully",
                        Map.of("id", id)
                )
        );
    }
}
