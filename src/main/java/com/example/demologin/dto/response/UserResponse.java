package com.example.demologin.dto.response;

import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String identityCard;
    private Gender gender;
    private UserStatus status;
    private LocalDateTime createdDate;
    private String token;
    private String refreshToken;
}
