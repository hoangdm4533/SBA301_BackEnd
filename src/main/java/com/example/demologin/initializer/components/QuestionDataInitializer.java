package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionDataInitializer {

    private final QuestionRepository questionRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public void initializeQuestions() {
        if (questionRepository.count() > 0) {
            log.info("ℹ️ Questions already exist, skip seeding.");
            return;
        }

        // ==== Kiểm tra Lesson ====
        List<Lesson> lessons = lessonRepository.findAll();
        if (lessons.isEmpty()) {
            throw new IllegalStateException("⚠️ No lessons found. Please seed Lesson data first.");
        }

        Lesson lesson1 = lessons.get(0);
        Lesson lesson2 = lessons.size() > 1 ? lessons.get(1) : lesson1;
        Lesson lesson3 = lessons.size() > 2 ? lessons.get(2) : lesson1;

        // ==== Lấy loại câu hỏi và độ khó ====
        QuestionType mcqSingle = questionTypeRepository.findByDescriptionIgnoreCase("MCQ_SINGLE")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: MCQ_SINGLE"));
        QuestionType mcqMulti = questionTypeRepository.findByDescriptionIgnoreCase("MCQ_MULTI")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: MCQ_MULTI"));
        QuestionType trueFalse = questionTypeRepository.findByDescriptionIgnoreCase("TRUE_FALSE")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: TRUE_FALSE"));
        QuestionType shortAns = questionTypeRepository.findByDescriptionIgnoreCase("SHORT_ANSWER")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: SHORT_ANSWER"));

        Level easy = levelRepository.findByDifficulty("EASY")
                .orElseThrow(() -> new IllegalStateException("Missing Level: EASY"));
        Level medium = levelRepository.findByDifficulty("MEDIUM")
                .orElseThrow(() -> new IllegalStateException("Missing Level: MEDIUM"));
        Level hard = levelRepository.findByDifficulty("HARD")
                .orElseThrow(() -> new IllegalStateException("Missing Level: HARD"));

        List<Question> questions = new ArrayList<>();

        // ===== CÂU HỎI MÔN TOÁN =====

        // Q1 - Số học cơ bản
        Question q1 = Question.builder()
                .questionText("Tổng của 125 + 379 bằng bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q1.setOptions(List.of(
                new Option(null, q1, "404", false),
                new Option(null, q1, "494", false),
                new Option(null, q1, "504", true),
                new Option(null, q1, "514", false)
        ));
        questions.add(q1);

        // Q2 - Bội số và ước số
        Question q2 = Question.builder()
                .questionText("Số nào là bội chung của 4 và 6?")
                .type(mcqMulti)
                .level(easy)
                .lesson(lesson1)
                .build();
        q2.setOptions(List.of(
                new Option(null, q2, "8", false),
                new Option(null, q2, "12", true),
                new Option(null, q2, "24", true),
                new Option(null, q2, "16", false)
        ));
        questions.add(q2);

        // Q3 - Dấu hiệu chia hết
        Question q3 = Question.builder()
                .questionText("Số 245 chia hết cho 5 đúng hay sai?")
                .type(trueFalse)
                .level(easy)
                .lesson(lesson1)
                .build();
        q3.setOptions(List.of(
                new Option(null, q3, "Đúng", true),
                new Option(null, q3, "Sai", false)
        ));
        questions.add(q3);

        // Q4 - Phân số
        Question q4 = Question.builder()
                .questionText("Rút gọn phân số \\( \\frac{12}{18} \\)")
                .type(mcqSingle)
                .level(medium)
                .lesson(lesson2)
                .formula("\\frac{12}{18}")
                .build();
        q4.setOptions(List.of(
                new Option(null, q4, "\\frac{2}{3}", true),
                new Option(null, q4, "\\frac{4}{6}", false),
                new Option(null, q4, "\\frac{6}{9}", false),
                new Option(null, q4, "\\frac{1}{3}", false)
        ));
        questions.add(q4);

        // Q5 - Thập phân
        Question q5 = Question.builder()
                .questionText("Kết quả của 0.25 × 8 là bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson2)
                .build();
        q5.setOptions(List.of(
                new Option(null, q5, "1", false),
                new Option(null, q5, "1.5", false),
                new Option(null, q5, "2", true),
                new Option(null, q5, "3", false)
        ));
        questions.add(q5);

        // Q6 - Biểu thức đại số
        Question q6 = Question.builder()
                .questionText("Giá trị của biểu thức 2x + 5 khi x = 3 là bao nhiêu?")
                .type(shortAns)
                .level(medium)
                .lesson(lesson2)
                .build();
        q6.setOptions(List.of(
                new Option(null, q6, "11", true)
        ));
        questions.add(q6);

        // Q7 - Phương trình đơn giản
        Question q7 = Question.builder()
                .questionText("Tìm x: 3x + 9 = 0")
                .type(shortAns)
                .level(medium)
                .lesson(lesson2)
                .build();
        q7.setOptions(List.of(
                new Option(null, q7, "x = -3", true)
        ));
        questions.add(q7);

        // Q8 - Hình học phẳng
        Question q8 = Question.builder()
                .questionText("Tổng ba góc của một tam giác bằng bao nhiêu độ?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson3)
                .build();
        q8.setOptions(List.of(
                new Option(null, q8, "90°", false),
                new Option(null, q8, "180°", true),
                new Option(null, q8, "270°", false),
                new Option(null, q8, "360°", false)
        ));
        questions.add(q8);

        // Q9 - Diện tích hình chữ nhật
        Question q9 = Question.builder()
                .questionText("Một hình chữ nhật có chiều dài 8cm và chiều rộng 5cm. Diện tích của nó là bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson3)
                .build();
        q9.setOptions(List.of(
                new Option(null, q9, "30 cm²", false),
                new Option(null, q9, "35 cm²", false),
                new Option(null, q9, "40 cm²", true),
                new Option(null, q9, "45 cm²", false)
        ));
        questions.add(q9);

        // Q10 - Thống kê
        Question q10 = Question.builder()
                .questionText("Cho dãy số: 2, 4, 6, 8, 10. Số trung bình cộng là bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson3)
                .build();
        q10.setOptions(List.of(
                new Option(null, q10, "5", true),
                new Option(null, q10, "6", false),
                new Option(null, q10, "7", false),
                new Option(null, q10, "8", false)
        ));
        questions.add(q10);

        // Q11 - Toán nâng cao
        Question q11 = Question.builder()
                .questionText("Kết quả của \\( (2 + 3)^2 - (3 - 2)^2 \\) là?")
                .type(mcqSingle)
                .level(hard)
                .lesson(lesson3)
                .formula("(2 + 3)^2 - (3 - 2)^2")
                .build();
        q11.setOptions(List.of(
                new Option(null, q11, "20", false),
                new Option(null, q11, "23", false),
                new Option(null, q11, "24", true),
                new Option(null, q11, "25", false)
        ));
        questions.add(q11);

        // Q12 - Số nguyên âm
        Question q12 = Question.builder()
                .questionText("Đúng hay sai: \\(-7 < -5\\)")
                .type(trueFalse)
                .level(easy)
                .lesson(lesson1)
                .build();
        q12.setOptions(List.of(
                new Option(null, q12, "Đúng", true),
                new Option(null, q12, "Sai", false)
        ));
        questions.add(q12);

        // ==== Lưu toàn bộ ====
        questionRepository.saveAll(questions);
        log.info("✅ Seeded {} Math questions linked to lessons.", questionRepository.count());
    }
}
