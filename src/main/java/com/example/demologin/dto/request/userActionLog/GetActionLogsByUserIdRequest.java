package com.example.demologin.dto.request.userActionLog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetActionLogsByUserIdRequest {
    
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be greater than 0")
    private Long userId;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 20;
}
