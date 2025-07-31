package com.example.demologin.dto.request.userActionLog;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetActionLogsByTypeRequest {
    
    @NotBlank(message = "Action type cannot be blank")
    private String actionType;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 20;
}
