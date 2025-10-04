package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
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
@RequestMapping("/api/admin/users")
@Tag(name = "User Management", description = "APIs for managing users (admin only)")
public class UserController {

    private final UserService userService;
    private final AccountUtils accountUtils;

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Users retrieved successfully")
    @SecuredEndpoint("USER_MANAGE")
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
    public MemberResponse get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public MemberResponse create(@Valid @RequestBody CreateUserRequest req) {
        return userService.create(req);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id,
                                 @Valid @RequestBody UpdateUserRequest req) {
        return userService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        userService.delete(id); // ném EntityNotFoundException nếu không tồn tại
        return ResponseEntity.ok(
                new ResponseObject(
                        HttpStatus.OK.value(),
                        "Delete user successfully",
                        Map.of("id", id)
                )
        );
    }
}
