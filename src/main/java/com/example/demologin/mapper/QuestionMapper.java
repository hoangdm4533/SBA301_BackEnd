package com.example.demologin.mapper;

import com.example.demologin.dto.request.question.OptionRequest;
import com.example.demologin.dto.response.OptionResponse;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {
    public QuestionResponse toResponse(Question q) {
        // Map danh sách Option sang OptionResponse
        List<OptionResponse> optionRes = (q.getOptions() == null ? List.of()
                : q.getOptions().stream()
                .map(o -> OptionResponse.builder()
                        .id(o.getId())
                        .optionText(o.getOptionText())
                        .isCorrect(o.getIsCorrect())
                        .build())
                .toList());

        // Truy xuất Lesson, Chapter, Grade và Level an toàn (null-safe)
        Long lessonId = null;
        String lessonName = null;
        Long chapterId = null;
        String chapterName = null;
        Long gradeId = null;
        Integer gradeNumber = null;
        Long levelId = null;
        String levelCode = null;
        Double levelScore = null;

        if (q.getLesson() != null) {
            lessonId = q.getLesson().getId();
            lessonName = q.getLesson().getLessonName();

            if (q.getLesson().getChapter() != null) {
                chapterId = q.getLesson().getChapter().getId();
                chapterName = q.getLesson().getChapter().getName();

                if (q.getLesson().getChapter().getGrade() != null) {
                    gradeId = q.getLesson().getChapter().getGrade().getId();
                    gradeNumber = q.getLesson().getChapter().getGrade().getGradeNumber();
                }
            }
        }

        if (q.getLevel() != null) {
            levelId = q.getLevel().getId();
            levelCode = q.getLevel().getDescription();
            levelScore = q.getLevel().getScore();
        }

        // Trả về DTO hoàn chỉnh cho FE
        return QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .type(q.getType() != null ? q.getType().getDescription() : null)
                .formula(q.getFormula())
                .lessonId(lessonId)
                .lessonName(lessonName)
                .chapterId(chapterId)
                .chapterName(chapterName)
                .gradeId(gradeId)
                .gradeNumber(gradeNumber)
                .levelId(levelId)
                .levelCode(levelCode)
                .levelScore(levelScore)
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .options(optionRes)
                .build();
    }

    public Option buildOption(Question q, OptionRequest req) {
        Option o = new Option();
        o.setQuestion(q);
        o.setOptionText(req.getOptionText());
        o.setIsCorrect(Boolean.TRUE.equals(req.getIsCorrect()));
        return o;
    }
}
