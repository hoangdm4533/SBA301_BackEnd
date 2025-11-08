package com.example.demologin.mapper.question;

import com.example.demologin.dto.request.question.OptionRequest;
import com.example.demologin.dto.response.OptionResponse;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper implements IQuestionMapper {
    @Override
    public QuestionResponse toResponse(Question q) {
        if (q == null) return null;

        // Map Option -> OptionResponse (giữ nguyên field như bạn đang dùng)
        List<OptionResponse> optionRes =
                (q.getOptions() == null || q.getOptions().isEmpty())
                        ? List.of()
                        : q.getOptions().stream()
                        .map(o -> OptionResponse.builder()
                                .id(o.getId())
                                .optionText(o.getOptionText())
                                .isCorrect(o.getIsCorrect())
                                .build())
                        .collect(Collectors.toList()); // dùng Collectors để tương thích JDK thấp

        // Null-safe truy xuất Lesson -> Chapter -> Grade
        Long lessonId = null;
        String lessonName = null;
        Long chapterId = null;
        String chapterName = null;
        Long gradeId = null;
        Integer gradeNumber = null;

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

        Long levelId = null;
        String levelCode = null;
        Double levelScore = null;
        if (q.getLevel() != null) {
            levelId = q.getLevel().getId();
            levelCode = q.getLevel().getDescription();
            levelScore = q.getLevel().getScore();
        }

        // Trả về đúng các field bạn đang dùng, không set dư
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

    @Override
    public Option buildOption(Question q, OptionRequest req) {
        if (q == null || req == null) return null;

        Option o = new Option();
        o.setQuestion(q);
        o.setOptionText(req.getOptionText());
        o.setIsCorrect(Boolean.TRUE.equals(req.getIsCorrect()));
        return o;
    }
}
