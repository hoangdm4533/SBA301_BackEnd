package com.example.demologin.dto.request.user;

import com.example.demologin.enums.Gender;
import com.example.demologin.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRegistrationRequest {
    @NotBlank(message = "Account cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm Password cannot be blank")
    private String confirmPassword;

    @NotBlank(message = "Full Name cannot be blank")
    private String fullName;

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Sex cannot be blank")
    private Gender gender;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Identity Card cannot be blank")
    private String identityCard;

    @NotBlank(message = "Phone Number cannot be blank")
    @Pattern(regexp = "\\d{10}", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}
