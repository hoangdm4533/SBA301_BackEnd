package com.example.demologin.dto.request.lesson_plan;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonPlanEditRequest {
    private Long lessonPlanId;
    private Long editorId;   // optional
    private String operation; // JSON string mô tả edit
}
