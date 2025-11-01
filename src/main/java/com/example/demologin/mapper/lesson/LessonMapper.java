package com.example.demologin.mapper.lesson;

import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper implements ILessonMapper {
    @Override
    public Lesson toEntity(LessonRequest request, Chapter chapter) {
        return Lesson.builder()
                .lessonName(request.getLessonName())
                .chapter(chapter)
                .build();
    }

    @Override
    public LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .lessonName(lesson.getLessonName())
                .chapterId(lesson.getChapter() != null ? lesson.getChapter().getId() : null)
                .chapterName(lesson.getChapter() != null ? lesson.getChapter().getName() : null)
                .build();
    }
}
