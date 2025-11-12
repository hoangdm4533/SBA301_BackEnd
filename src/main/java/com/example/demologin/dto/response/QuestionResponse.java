package com.example.demologin.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String type;

    private Long lessonId;          // tiện cho FE
    private String lessonName;

    private Long chapterId;         // suy ra từ lesson.getChapter()
    private String chapterName;

    private Long gradeId;           // suy ra từ lesson.getChapter().getGrade()
    private Integer gradeNumber;

    private Long levelId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OptionResponse> options;
}
