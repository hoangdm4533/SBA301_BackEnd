package com.example.demologin.mapper;

import com.example.demologin.dto.request.user.UserRequest;
import com.example.demologin.dto.response.LoginResponse;
import com.example.demologin.dto.response.MemberResponse;
import com.example.demologin.dto.response.UserResponse;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.User;
import com.example.demologin.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public MemberResponse toUserResponse(User u) {
        MemberResponse r = new MemberResponse();
        r.setUserId(u.getUserId());
        r.setUsername(u.getUsername());
        r.setFullName(u.getFullName());
        r.setEmail(u.getEmail());
        r.setGender(u.getGender());
        r.setStatus(u.getStatus());
        r.setLocked(u.isLocked());
        r.setVerify(u.isVerify());
        r.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        if (u.getClassEntity() != null) {
            r.setClassId(u.getClassEntity().getId());
            r.setClassName(u.getClassEntity().getName());
        }
        return r;
    }
    public static User toEntity(UserRequest userRequest) {
        User user = new User(
                userRequest.getUsername(),
                null, // password will be set elsewhere
                userRequest.getFullname(),
                userRequest.getEmail()
        );
        user.setStatus(UserStatus.ACTIVE); // Default status
        
        return user;
    }

    // Convert User -> LoginResponse (cho login) - chỉ trả về token và refreshToken
    public static LoginResponse toLoginResponse(User user, String token, String refreshToken) {
        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }
}
