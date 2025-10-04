package com.example.demologin.dto.request.user;

import com.example.demologin.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank
    @Size(max = 28)
    private String username;

    @NotBlank @Size(min = 6, max = 128)
    private String password;

    @NotBlank @Size(max = 100)
    private String fullName;

    @Email
    @NotBlank @Size(max = 255)
    private String email;

    private Gender gender;

    @NotEmpty
    private Set<String> roles;

    // gán vào lớp
    private Long classId;
}
