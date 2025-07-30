package com.example.demologin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountLockoutResponse {
    private String username;
    private boolean isLocked;
    private LocalDateTime lockTime;
    private LocalDateTime unlockTime;
    private String reason;
    private long remainingMinutes;
    private LocalDateTime checkedAt;
}
