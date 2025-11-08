package com.example.demologin.dto.response;

import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberResponse {
    private String role;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private UserStatus status;
    private Set<String> roles;
    private boolean locked;
    private boolean verify;
    private Long classId;
    private String className;
    private Gender gender;

    public MemberResponse(Long userId, String username, String email, String fullName, UserStatus status, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.role = role;
    }
}

