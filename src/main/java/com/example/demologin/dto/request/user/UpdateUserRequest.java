package com.example.demologin.dto.request.user;

import com.example.demologin.enums.Gender;
import com.example.demologin.enums.UserStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateUserRequest {
    private Long userId;
    private String fullName;
    private String email;
    private String reason; // Lý do cập nhật
    private UserStatus status;
    private Boolean locked;
    private Boolean verify;
    private Gender gender;
    private Set<String> roles;
    private Long classId;        // id lớp học gán cho user
    @Size(min = 6, max = 128)
    private String newPassword;
}
