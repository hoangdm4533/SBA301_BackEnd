package com.example.demologin.dto.request.lesson;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {
    private String lessonName;
    private Long chapterId;
}
