package com.example.demologin.dto.request.userActionLog;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteActionLogRequest {
    
    @NotNull(message = "Action log ID cannot be null")
    @Min(value = 1, message = "Action log ID must be greater than 0")
    private Long id;
}
