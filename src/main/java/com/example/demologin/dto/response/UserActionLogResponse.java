package com.example.demologin.dto.response;

import com.example.demologin.enums.UserActionType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String roleName;
    private UserActionType actionType;
    private String targetType;
    private String targetId;
    private String targetName;
    private String description;
    private String reason;
    private String changeSummary;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime actionTime;
}
