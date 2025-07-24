package com.example.demologin.mapper;

import com.example.demologin.dto.request.UserRequest;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.User;
import com.example.demologin.enums.Role;
import com.example.demologin.enums.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public MemberResponse toUserResponse(User user) {
        return new MemberResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getIdentityCard(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getStatus(),
                user.getRole()
        );
    }
    public static User toEntity(UserRequest userRequest) {
        return User.builder()
                .username(userRequest.getUsername())
                .fullName(userRequest.getFullname())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .address(userRequest.getAddress())
                .dateOfBirth(userRequest.getDateOfBirth())
                .identityCard(userRequest.getIdentity_Card())
                .role(Role.MEMBER) // Default role
                .status(UserStatus.ACTIVE) // Default status
                .build();
    }

    // Convert User -> UserResponse (cho login)
    public static  UserResponse toResponse(User user, String token, String refreshToken) {
        return UserResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }
}
