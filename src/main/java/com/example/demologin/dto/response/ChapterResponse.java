package com.example.demologin.dto.response;

import com.example.demologin.dto.response.lesson.LessonResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterResponse {
    private Long id;
    private Integer gradeNumber;
    private String name;
    private Integer orderNo;
    private List<LessonResponse> lessons;
}
