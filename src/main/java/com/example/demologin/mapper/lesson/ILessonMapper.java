package com.example.demologin.mapper.lesson;

import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Lesson;

public interface ILessonMapper {
    Lesson toEntity(LessonRequest request, Chapter chapter);
    LessonResponse toResponse(Lesson lesson);
}
