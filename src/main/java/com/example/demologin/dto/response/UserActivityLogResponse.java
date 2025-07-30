package com.example.demologin.dto.response;

import com.example.demologin.enums.ActivityType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLogResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("activityType")
    private ActivityType activityType;

    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("username")
    private String username;

    @JsonProperty("editorId")
    private Long editorId;
    
    @JsonProperty("editorUsername")
    private String editorUsername;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("details")
    private String details;
    
    @JsonProperty("ipAddress")
    private String ipAddress;
    
    @JsonProperty("userAgent")
    private String userAgent;
}
