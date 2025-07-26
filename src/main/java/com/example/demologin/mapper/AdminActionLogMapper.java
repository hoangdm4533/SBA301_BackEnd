package com.example.demologin.mapper;

import com.example.demologin.dto.response.AdminActionLogResponse;
import com.example.demologin.entity.AdminActionLog;
import org.springframework.stereotype.Component;

@Component
public class AdminActionLogMapper {
    public AdminActionLogResponse toResponseDto(AdminActionLog entity) {
        AdminActionLogResponse dto = new AdminActionLogResponse();
        dto.setId(entity.getId());
        dto.setAdminId(entity.getAdminId());
        dto.setTargetType(entity.getTargetType());
        dto.setTargetId(entity.getTargetId());
        dto.setActionType(entity.getActionType());
        dto.setReason(entity.getReason());
        dto.setChangeSummary(entity.getChangeSummary());
        dto.setActionTime(entity.getActionTime());
        return dto;
    }
}
