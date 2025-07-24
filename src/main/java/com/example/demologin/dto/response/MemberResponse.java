package com.example.demologin.dto.response;

import com.example.demologin.enums.Role;
import com.example.demologin.enums.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

import com.example.demologin.enums.Role;
import com.example.demologin.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long userId;
    private String username;
    private String email;
    private String identity_Card;
    private String fullName;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private Role role;
}

