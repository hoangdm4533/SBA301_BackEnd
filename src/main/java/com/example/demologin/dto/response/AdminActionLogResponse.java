package com.example.demologin.dto.response;

import com.example.demologin.enums.AdminActionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminActionLogResponse {
    private Long id;
    private Long adminId;
    private String targetType;
    private String targetId;
    private AdminActionType actionType;
    private String reason;
    private String changeSummary;
    private LocalDateTime actionTime;
}
