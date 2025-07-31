package com.example.demologin.dto.request.role;
import com.example.demologin.dto.request.BaseActionRequest;

import com.example.demologin.dto.request.BaseActionRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRoleRequest extends BaseActionRequest {
    // reason field inherited from BaseActionRequest
    // Will be auto-populated when @UserAction(requiresReason = true)
    
    // Additional fields for delete confirmation can be added here
    private boolean confirmed = false;
}
