package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionDataInitializer {

    QuestionRepository questionRepository;
    QuestionTypeRepository questionTypeRepository;
    LevelRepository levelRepository;
    LessonRepository lessonRepository;

    @Transactional
    public void initializeQuestions() {
        if (questionRepository.count() > 0) {
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

        Level easy = levelRepository.findByDifficulty("EASY")
                .orElseThrow(() -> new IllegalStateException("Missing Level: EASY"));
        Level medium = levelRepository.findByDifficulty("MEDIUM")
                .orElseThrow(() -> new IllegalStateException("Missing Level: MEDIUM"));
        Level hard = levelRepository.findByDifficulty("HARD")
                .orElseThrow(() -> new IllegalStateException("Missing Level: HARD"));

        List<Question> questions = new ArrayList<>();

        // ===== 10 CÂU HỎI: LEVEL 1 (EASY) + MCQ_SINGLE + LESSON 1 =====

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

        Question q2 = Question.builder()
                .questionText("Hiệu của 500 - 237 bằng bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q2.setOptions(List.of(
                new Option(null, q2, "253", false),
                new Option(null, q2, "263", true),
                new Option(null, q2, "273", false),
                new Option(null, q2, "283", false)
        ));
        questions.add(q2);

        Question q3 = Question.builder()
                .questionText("Tích của 12 × 8 bằng bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q3.setOptions(List.of(
                new Option(null, q3, "84", false),
                new Option(null, q3, "92", false),
                new Option(null, q3, "96", true),
                new Option(null, q3, "100", false)
        ));
        questions.add(q3);

        Question q4 = Question.builder()
                .questionText("144 chia cho 12 bằng bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q4.setOptions(List.of(
                new Option(null, q4, "10", false),
                new Option(null, q4, "11", false),
                new Option(null, q4, "12", true),
                new Option(null, q4, "13", false)
        ));
        questions.add(q4);

        Question q5 = Question.builder()
                .questionText("Số nào là số chẵn?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q5.setOptions(List.of(
                new Option(null, q5, "13", false),
                new Option(null, q5, "17", false),
                new Option(null, q5, "18", true),
                new Option(null, q5, "21", false)
        ));
        questions.add(q5);

        Question q6 = Question.builder()
                .questionText("Số nào là số nguyên tố?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q6.setOptions(List.of(
                new Option(null, q6, "4", false),
                new Option(null, q6, "7", true),
                new Option(null, q6, "9", false),
                new Option(null, q6, "15", false)
        ));
        questions.add(q6);

        Question q7 = Question.builder()
                .questionText("25% của 200 bằng bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q7.setOptions(List.of(
                new Option(null, q7, "25", false),
                new Option(null, q7, "40", false),
                new Option(null, q7, "50", true),
                new Option(null, q7, "75", false)
        ));
        questions.add(q7);

        Question q8 = Question.builder()
                .questionText("Số nào lớn nhất trong các số sau: 234, 324, 243, 342?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q8.setOptions(List.of(
                new Option(null, q8, "234", false),
                new Option(null, q8, "324", false),
                new Option(null, q8, "243", false),
                new Option(null, q8, "342", true)
        ));
        questions.add(q8);

        Question q9 = Question.builder()
                .questionText("Chu vi hình vuông có cạnh 5cm là bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q9.setOptions(List.of(
                new Option(null, q9, "10 cm", false),
                new Option(null, q9, "15 cm", false),
                new Option(null, q9, "20 cm", true),
                new Option(null, q9, "25 cm", false)
        ));
        questions.add(q9);

        Question q10 = Question.builder()
                .questionText("Diện tích hình vuông có cạnh 6cm là bao nhiêu?")
                .type(mcqSingle)
                .level(easy)
                .lesson(lesson1)
                .build();
        q10.setOptions(List.of(
                new Option(null, q10, "24 cm²", false),
                new Option(null, q10, "30 cm²", false),
                new Option(null, q10, "36 cm²", true),
                new Option(null, q10, "42 cm²", false)
        ));
        questions.add(q10);

        // ===== 10 CÂU HỎI: LEVEL 2 (MEDIUM) + MCQ_MULTI + LESSON 2 =====

        Question q11 = Question.builder()
                .questionText("Số nào là bội của 6?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q11.setOptions(List.of(
                new Option(null, q11, "12", true),
                new Option(null, q11, "15", false),
                new Option(null, q11, "18", true),
                new Option(null, q11, "20", false)
        ));
        questions.add(q11);

        Question q12 = Question.builder()
                .questionText("Số nào là ước của 24?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q12.setOptions(List.of(
                new Option(null, q12, "3", true),
                new Option(null, q12, "5", false),
                new Option(null, q12, "6", true),
                new Option(null, q12, "8", true)
        ));
        questions.add(q12);

        Question q13 = Question.builder()
                .questionText("Phân số nào bằng 1/2?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q13.setOptions(List.of(
                new Option(null, q13, "2/4", true),
                new Option(null, q13, "3/5", false),
                new Option(null, q13, "4/8", true),
                new Option(null, q13, "5/10", true)
        ));
        questions.add(q13);

        Question q14 = Question.builder()
                .questionText("Số nào chia hết cho cả 2 và 3?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q14.setOptions(List.of(
                new Option(null, q14, "6", true),
                new Option(null, q14, "9", false),
                new Option(null, q14, "12", true),
                new Option(null, q14, "15", false)
        ));
        questions.add(q14);

        Question q15 = Question.builder()
                .questionText("Hình nào có 4 cạnh bằng nhau?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q15.setOptions(List.of(
                new Option(null, q15, "Hình vuông", true),
                new Option(null, q15, "Hình chữ nhật", false),
                new Option(null, q15, "Hình thoi", true),
                new Option(null, q15, "Hình thang", false)
        ));
        questions.add(q15);

        Question q16 = Question.builder()
                .questionText("Số nào là số lẻ?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q16.setOptions(List.of(
                new Option(null, q16, "11", true),
                new Option(null, q16, "14", false),
                new Option(null, q16, "17", true),
                new Option(null, q16, "20", false)
        ));
        questions.add(q16);

        Question q17 = Question.builder()
                .questionText("Đơn vị nào dùng để đo độ dài?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q17.setOptions(List.of(
                new Option(null, q17, "Mét", true),
                new Option(null, q17, "Kilogram", false),
                new Option(null, q17, "Centimét", true),
                new Option(null, q17, "Lít", false)
        ));
        questions.add(q17);

        Question q18 = Question.builder()
                .questionText("Số nào là bội chung của 4 và 6?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q18.setOptions(List.of(
                new Option(null, q18, "8", false),
                new Option(null, q18, "12", true),
                new Option(null, q18, "24", true),
                new Option(null, q18, "16", false)
        ));
        questions.add(q18);

        Question q19 = Question.builder()
                .questionText("Hình nào có tất cả các góc đều là góc vuông?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q19.setOptions(List.of(
                new Option(null, q19, "Hình vuông", true),
                new Option(null, q19, "Hình chữ nhật", true),
                new Option(null, q19, "Hình thoi", false),
                new Option(null, q19, "Hình tam giác", false)
        ));
        questions.add(q19);

        Question q20 = Question.builder()
                .questionText("Số nào nhỏ hơn 10?")
                .type(mcqMulti)
                .level(medium)
                .lesson(lesson2)
                .build();
        q20.setOptions(List.of(
                new Option(null, q20, "5", true),
                new Option(null, q20, "8", true),
                new Option(null, q20, "12", false),
                new Option(null, q20, "15", false)
        ));
        questions.add(q20);

        // ===== 10 CÂU HỎI: LEVEL 3 (HARD) + TRUE_FALSE + LESSON 3 =====

        Question q21 = Question.builder()
                .questionText("Số 245 chia hết cho 5 đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q21.setOptions(List.of(
                new Option(null, q21, "Đúng", true),
                new Option(null, q21, "Sai", false)
        ));
        questions.add(q21);

        Question q22 = Question.builder()
                .questionText("Tổng các góc của một tứ giác bằng 360° đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q22.setOptions(List.of(
                new Option(null, q22, "Đúng", true),
                new Option(null, q22, "Sai", false)
        ));
        questions.add(q22);

        Question q23 = Question.builder()
                .questionText("Số 0 là số chẵn đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q23.setOptions(List.of(
                new Option(null, q23, "Đúng", true),
                new Option(null, q23, "Sai", false)
        ));
        questions.add(q23);

        Question q24 = Question.builder()
                .questionText("1 km = 1000 m đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q24.setOptions(List.of(
                new Option(null, q24, "Đúng", true),
                new Option(null, q24, "Sai", false)
        ));
        questions.add(q24);

        Question q25 = Question.builder()
                .questionText("Số nguyên tố nhỏ nhất là 1 đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q25.setOptions(List.of(
                new Option(null, q25, "Đúng", false),
                new Option(null, q25, "Sai", true)
        ));
        questions.add(q25);

        Question q26 = Question.builder()
                .questionText("Hình tròn có vô số trục đối xứng đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q26.setOptions(List.of(
                new Option(null, q26, "Đúng", true),
                new Option(null, q26, "Sai", false)
        ));
        questions.add(q26);

        Question q27 = Question.builder()
                .questionText("\\(-7 < -5\\) đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q27.setOptions(List.of(
                new Option(null, q27, "Đúng", true),
                new Option(null, q27, "Sai", false)
        ));
        questions.add(q27);

        Question q28 = Question.builder()
                .questionText("Tất cả các số chia hết cho 6 đều chia hết cho 3 đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q28.setOptions(List.of(
                new Option(null, q28, "Đúng", true),
                new Option(null, q28, "Sai", false)
        ));
        questions.add(q28);

        Question q29 = Question.builder()
                .questionText("Diện tích hình tam giác = (đáy × chiều cao) / 2 đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q29.setOptions(List.of(
                new Option(null, q29, "Đúng", true),
                new Option(null, q29, "Sai", false)
        ));
        questions.add(q29);

        Question q30 = Question.builder()
                .questionText("Phân số 3/4 lớn hơn 2/3 đúng hay sai?")
                .type(trueFalse)
                .level(hard)
                .lesson(lesson3)
                .build();
        q30.setOptions(List.of(
                new Option(null, q30, "Đúng", true),
                new Option(null, q30, "Sai", false)
        ));
        questions.add(q30);

        // ==== Lưu toàn bộ ====
        questionRepository.saveAll(questions);
    }
}
