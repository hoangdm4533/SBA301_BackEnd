package com.example.demologin.mapper;

import com.example.demologin.dto.response.UserActionLogResponse;
import com.example.demologin.entity.UserActionLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserActionLogMapper {
    
    public UserActionLogResponse toResponse(UserActionLog userActionLog) {
        if (userActionLog == null) {
            return null;
        }
        
        return UserActionLogResponse.builder()
                .id(userActionLog.getId())
                .userId(userActionLog.getUserId())
                .username(userActionLog.getUsername())
                .roleName(userActionLog.getRoleName())
                .actionType(userActionLog.getActionType())
                .targetType(userActionLog.getTargetType())
                .targetId(userActionLog.getTargetId())
                .targetName(userActionLog.getTargetName())
                .description(userActionLog.getDescription())
                .reason(userActionLog.getReason())
                .changeSummary(userActionLog.getChangeSummary())
                .ipAddress(userActionLog.getIpAddress())
                .userAgent(userActionLog.getUserAgent())
                .actionTime(userActionLog.getActionTime())
                .build();
    }
    
    public List<UserActionLogResponse> toResponseList(List<UserActionLog> userActionLogs) {
        if (userActionLogs == null) {
            return null;
        }
        
        return userActionLogs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
