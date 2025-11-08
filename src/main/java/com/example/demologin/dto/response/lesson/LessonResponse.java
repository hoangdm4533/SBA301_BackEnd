package com.example.demologin.dto.response.lesson;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private Long id;
    private String lessonName;
    private Long chapterId;
    private String chapterName;
}
