package com.example.demologin.initializer.components;

import com.example.demologin.entity.ExamQuestion;
import com.example.demologin.entity.ExamTemplate;
import com.example.demologin.entity.Question;
import com.example.demologin.repository.ExamQuestionRepository;
import com.example.demologin.repository.ExamTemplateRepository;
import com.example.demologin.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(9)
public class ExamQuestionDataInitializer {

    private final ExamTemplateRepository examTemplateRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Transactional
    public void initialize() {
        log.debug("🔗 Creating exam question relationships...");
        
        if (examQuestionRepository.count() > 0) {
            log.info("ℹ️ Exam questions already exist, skipping initialization");
            return;
        }

        List<ExamTemplate> templates = examTemplateRepository.findAllWithLevel();
        List<Question> allQuestions = questionRepository.findAll();
        
        if (templates.isEmpty() || allQuestions.isEmpty()) {
            log.warn("⚠️ No exam templates or questions found, skipping exam question initialization");
            return;
        }

        for (ExamTemplate template : templates) {
            createExamQuestionsForTemplate(template, allQuestions);
        }
        
        log.debug("✅ Created {} exam question relationships", examQuestionRepository.count());
    }

    private void createExamQuestionsForTemplate(ExamTemplate template, List<Question> allQuestions) {
        String templateDifficulty = template.getLevel().getName();
        
        // Lấy câu hỏi theo độ khó phù hợp với template
        List<Question> suitableQuestions = questionRepository.findByDifficulty(templateDifficulty);
        
        // Nếu không có câu hỏi phù hợp, dùng một số câu hỏi bất kỳ
        if (suitableQuestions.isEmpty()) {
            suitableQuestions = allQuestions.subList(0, Math.min(5, allQuestions.size()));
        } else {
            // Giới hạn số câu hỏi
            suitableQuestions = suitableQuestions.subList(0, Math.min(10, suitableQuestions.size()));
        }

        int order = 1;
        for (Question question : suitableQuestions) {
            ExamQuestion examQuestion = ExamQuestion.builder()
                .examTemplate(template)
                .question(question)
                .questionOrder(order++)
                .points(calculatePointsForQuestion(question))
                .build();
            
            examQuestionRepository.save(examQuestion);
        }
        
        log.debug("✅ Added {} questions to template: {}", suitableQuestions.size(), template.getTitle());
    }

    private Double calculatePointsForQuestion(Question question) {
        return switch (question.getDifficulty()) {
            case "Beginner" -> 1.0;
            case "Intermediate" -> 2.0;
            case "Advanced" -> 3.0;
            case "Expert" -> 5.0;
            default -> 1.0;
        };
    }
}