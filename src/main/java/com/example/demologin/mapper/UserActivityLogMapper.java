package com.example.demologin.mapper;

import com.example.demologin.dto.response.UserActivityLogResponse;
import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserActivityLogMapper {

    private final UserRepository userRepository;

    public UserActivityLogResponse toResponse(UserActivityLog log) {
        UserActivityLogResponse response = new UserActivityLogResponse();
        response.setId(log.getId());
        response.setActivityType(log.getActivityType());
        response.setUserId(log.getUserId());
        response.setEditorId(log.getEditorId());
        response.setTimestamp(log.getTimestamp());
        response.setStatus(log.getStatus());
        response.setDetails(log.getDetails());
        response.setIpAddress(log.getIpAddress());
        
        // Set username if userId exists
        if (log.getUserId() != null) {
            userRepository.findById(log.getUserId())
                .ifPresent(user -> response.setUsername(user.getUsername()));
        }
        
        // Set editor username if editorId exists
        if (log.getEditorId() != null) {
            userRepository.findById(log.getEditorId())
                .ifPresent(editor -> response.setEditorUsername(editor.getUsername()));
        }
        
        return response;
    }

    public List<UserActivityLogResponse> toResponseList(List<UserActivityLog> logs) {
        return logs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
