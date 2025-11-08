package com.example.demologin.service;

import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.lesson.LessonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonService {
    LessonResponse createLesson(LessonRequest request);
    LessonResponse getLessonById(Long id);
    List<LessonResponse> getAllLessons();
    LessonResponse updateLesson(Long id, LessonRequest request);
    boolean deleteLesson(Long id);
    Page<LessonResponse> getLessonsPage(Pageable pageable);
    List<LessonResponse> getLessonsByChapterId(Long chapterId);
}
