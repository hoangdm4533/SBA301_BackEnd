package com.example.demologin.dto.request.user;

import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AdminUpdateUserRequest {

    @Size(max = 50)
    private String username;

    @Size(max = 100)
    private String fullName;

    @Email
    @Size(max = 255)
    private String email;

    private Gender gender;

    @Size(min = 6, max = 100)
    private String newPassword;

    // Admin-only
    private UserStatus status;
    private Boolean locked;
    private Boolean verify;
    private Set<String> roles;

    @Size(max = 500)
    private String reason;
}
