package com.example.demologin.initializer.components;

import com.example.demologin.entity.Exam;
import com.example.demologin.entity.ExamQuestion;
import com.example.demologin.entity.Question;
import com.example.demologin.repository.ExamQuestionRepository;
import com.example.demologin.repository.ExamRepository;
import com.example.demologin.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamDataInitializer {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public void initializeExams() {
        if (examRepository.count() > 0) {
            log.info("ℹ️ Exams already exist, skip seeding.");
            return;
        }

        List<Question> questions = questionRepository.findAll();
        if (questions.size() < 3) {
            log.warn("⚠️ Need at least 3 questions to seed sample exams. Found: {}", questions.size());
            return;
        }

        // Exam 1 - DRAFT
        Exam exam1 = Exam.builder()
                .title("Math Basics – Draft")
                .description("Simple arithmetic and number properties")
                .status("DRAFT") // sẽ dùng API publish sau
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exam1 = examRepository.save(exam1);

        // Gán 3 câu đầu vào exam 1
        addQuestion(exam1, questions.get(0), 1.0);
        if (questions.size() > 1) addQuestion(exam1, questions.get(1), 1.0);
        if (questions.size() > 2) addQuestion(exam1, questions.get(2), 1.0);

        // Exam 2 - PUBLISHED
        Exam exam2 = Exam.builder()
                .title("General Knowledge – Published")
                .description("Mixed MCQ and True/False")
                .status("PUBLISHED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exam2 = examRepository.save(exam2);

        // Gán 3 câu
        addQuestion(exam2, questions.get(0), 1.0);
        if (questions.size() > 3) addQuestion(exam2, questions.get(3), 1.0);
        if (questions.size() > 4) addQuestion(exam2, questions.get(4), 1.0);

        log.info("✅ Seeded sample exams: {}, total exams now: {}", 2, examRepository.count());
    }

    private void addQuestion(Exam exam, Question question, double score) {
        ExamQuestion eq = ExamQuestion.builder()
                .exam(exam)
                .question(question)
                .score(score)
                .build();
        examQuestionRepository.save(eq);
    }
}
