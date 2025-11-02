package com.example.demologin.mapper.chapter;

import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.mapper.lesson.ILessonMapper;
import com.example.demologin.mapper.lesson.LessonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChapterMapper implements IChapterMapper {
    private final ILessonMapper lessonMapper;
    @Override
    public ChapterResponse toResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .gradeNumber(chapter.getGrade() != null ? chapter.getGrade().getGradeNumber() : null)
                .name(chapter.getName())
                .orderNo(chapter.getOrderNo())
                .lessons(chapter.getLessons() != null ?
                        chapter.getLessons()
                                .stream()
                                .map(lessonMapper::toResponse)
                                .collect(Collectors.toList()) : Collections.emptyList() )
                .build();
    }
}
