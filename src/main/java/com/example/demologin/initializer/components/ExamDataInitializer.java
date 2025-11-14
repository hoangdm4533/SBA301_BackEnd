package com.example.demologin.initializer.components;

import com.example.demologin.entity.Exam;
import com.example.demologin.entity.ExamQuestion;
import com.example.demologin.entity.Question;
import com.example.demologin.repository.ExamQuestionRepository;
import com.example.demologin.repository.ExamRepository;
import com.example.demologin.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamDataInitializer {
    ExamRepository examRepository;
    ExamQuestionRepository examQuestionRepository;
    QuestionRepository questionRepository;

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
                .status("PUBLISHED")
                .durationMinutes(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exam1 = examRepository.save(exam1);

        // Gán 5 câu đầu vào exam 1 nếu có
        if (!questions.isEmpty()) addQuestion(exam1, questions.get(0), 1.0);
        if (questions.size() > 1) addQuestion(exam1, questions.get(1), 1.0);
        if (questions.size() > 2) addQuestion(exam1, questions.get(2), 1.0);
        if (questions.size() > 3) addQuestion(exam1, questions.get(3), 1.0);
        if (questions.size() > 4) addQuestion(exam1, questions.get(4), 1.0);

        // Exam 2 - PUBLISHED
        Exam exam2 = Exam.builder()
                .title("General Knowledge – Published")
                .description("Mixed MCQ and True/False")
                .status("PUBLISHED")
                .durationMinutes(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exam2 = examRepository.save(exam2);

        // Gán 4 câu khác cho exam 2
        if (questions.size() > 5) addQuestion(exam2, questions.get(5), 1.0);
        if (questions.size() > 6) addQuestion(exam2, questions.get(6), 1.0);
        if (questions.size() > 7) addQuestion(exam2, questions.get(7), 1.0);
        if (questions.size() > 8) addQuestion(exam2, questions.get(8), 1.0);

        // Exam 3 - ARCHIVED
        Exam exam3 = Exam.builder()
                .title("Science & Geography – Archived")
                .description("Archived sample exam")
                .status("ARCHIVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        exam3 = examRepository.save(exam3);

        // Gán thêm một vài câu cho exam 3
        if (questions.size() > 9) addQuestion(exam3, questions.get(9), 1.0);
        if (questions.size() > 10) addQuestion(exam3, questions.get(10), 1.0);
        if (questions.size() > 11) addQuestion(exam3, questions.get(11), 1.0);

        log.info("✅ Seeded sample exams: {}, total exams now: {}", 3, examRepository.count());
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
