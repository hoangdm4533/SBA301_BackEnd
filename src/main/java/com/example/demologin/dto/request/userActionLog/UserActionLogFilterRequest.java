package com.example.demologin.dto.request.userActionLog;

import com.example.demologin.enums.UserActionType;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionLogFilterRequest {
    private Long userId;
    private String username;
    private UserActionType actionType;
    private String targetType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    @Builder.Default
    private String sortBy = "actionTime";
    @Builder.Default
    private String sortDirection = "DESC";
}
