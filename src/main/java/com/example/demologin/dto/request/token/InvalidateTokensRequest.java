package com.example.demologin.dto.request.token;

import com.example.demologin.dto.request.BaseActionRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidateTokensRequest extends BaseActionRequest {
    
    @NotBlank(message = "Reason is required for token invalidation")
    private String reason;
    
    // Optional: Specify if this should logout from all devices or current device only
    private boolean logoutAllDevices = true;
    
    // Optional: Additional context for the invalidation
    private String context;
}
