package com.example.demologin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonPlanEditResponse {
    private Long id;
    private String operation;
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdAt;
}
