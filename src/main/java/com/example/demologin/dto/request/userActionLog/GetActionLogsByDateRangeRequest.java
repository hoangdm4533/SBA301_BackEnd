package com.example.demologin.dto.request.userActionLog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetActionLogsByDateRangeRequest {
    
    @NotBlank(message = "Start date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Start date must be in format YYYY-MM-DD")
    private String startDate;
    
    @NotBlank(message = "End date cannot be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "End date must be in format YYYY-MM-DD")
    private String endDate;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 20;
}
