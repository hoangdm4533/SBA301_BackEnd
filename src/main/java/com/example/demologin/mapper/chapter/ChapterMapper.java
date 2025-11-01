package com.example.demologin.mapper.chapter;

import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.entity.Chapter;
import org.springframework.stereotype.Component;

@Component
public class ChapterMapper implements IChapterMapper {
    @Override
    public ChapterResponse toResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .gradeNumber(chapter.getGrade() != null ? chapter.getGrade().getGradeNumber() : null)
                .name(chapter.getName())
                .orderNo(chapter.getOrderNo())
                .build();
    }
}
