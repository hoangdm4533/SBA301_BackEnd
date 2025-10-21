package com.example.demologin.initializer.components;

import com.example.demologin.entity.Level;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import com.example.demologin.entity.QuestionType;
import com.example.demologin.repository.LevelRepository;
import com.example.demologin.repository.QuestionRepository;
import com.example.demologin.repository.QuestionTypeRepository;
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

    @Transactional
    public void initializeQuestions() {
        if (questionRepository.count() > 0) {
            log.info("ℹ️ Questions already exist, skip seeding.");
            return;
        }

        QuestionType mcqSingle = questionTypeRepository.findByDescriptionIgnoreCase("MCQ_SINGLE")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: MCQ_SINGLE"));
        QuestionType mcqMulti = questionTypeRepository.findByDescriptionIgnoreCase("MCQ_MULTI")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: MCQ_MULTI"));
        QuestionType trueFalse = questionTypeRepository.findByDescriptionIgnoreCase("TRUE_FALSE")
                .orElseThrow(() -> new IllegalStateException("Missing QuestionType: TRUE_FALSE"));

        Level easy = levelRepository.findByDescription("EASY")
                .orElseThrow(() -> new IllegalStateException("Missing Level: EASY"));
        Level medium = levelRepository.findByDescription("MEDIUM")
                .orElseThrow(() -> new IllegalStateException("Missing Level: MEDIUM"));

        // Q1: MCQ_SINGLE
        Question q1 = Question.builder()
                .questionText("Which number is a prime?")
                .type(mcqSingle)
                .level(easy)
                .formula(null) // nếu bạn dùng công thức LaTeX, có thể set ví dụ "\\frac{a}{b}"
                .build();

        List<Option> q1Opts = new ArrayList<>();
        q1Opts.add(new Option(null, q1, "9", false));
        q1Opts.add(new Option(null, q1, "15", false));
        q1Opts.add(new Option(null, q1, "17", true));
        q1Opts.add(new Option(null, q1, "21", false));
        q1.setOptions(q1Opts);

        // Q2: MCQ_MULTI
        Question q2 = Question.builder()
                .questionText("Select all even numbers.")
                .type(mcqMulti)
                .level(easy)
                .build();

        List<Option> q2Opts = new ArrayList<>();
        q2Opts.add(new Option(null, q2, "3", false));
        q2Opts.add(new Option(null, q2, "6", true));
        q2Opts.add(new Option(null, q2, "12", true));
        q2Opts.add(new Option(null, q2, "19", false));
        q2.setOptions(q2Opts);

        // Q3: TRUE_FALSE
        Question q3 = Question.builder()
                .questionText("The Earth revolves around the Sun.")
                .type(trueFalse)
                .level(medium)
                .build();

        List<Option> q3Opts = new ArrayList<>();
        q3Opts.add(new Option(null, q3, "True", true));
        q3Opts.add(new Option(null, q3, "False", false));
        q3.setOptions(q3Opts);

        Question q4 = Question.builder()
                .questionText("What is the value of the expression?")
                .formula("\\frac{3}{4} + \\frac{5}{6}") // hiển thị LaTeX: ¾ + ⁵⁄₆
                .type(mcqSingle)
                .level(medium)
                .build();
        q4.setOptions(List.of(
                new Option(null, q4, "\\frac{19}{12}", true),
                new Option(null, q4, "\\frac{1}{2}", false),
                new Option(null, q4, "\\frac{7}{8}", false)
        ));

        // Q5: MCQ_SINGLE (đảm bảo có ít nhất 5 câu hỏi cho exam seeder)
        Question q5 = Question.builder()
                .questionText("Which planet is known as the Red Planet?")
                .type(mcqSingle)
                .level(easy)
                .build();
        q5.setOptions(List.of(
                new Option(null, q5, "Earth", false),
                new Option(null, q5, "Mars", true),
                new Option(null, q5, "Venus", false),
                new Option(null, q5, "Jupiter", false)
        ));

        // Q6: MCQ_MULTI
        Question q6 = Question.builder()
                .questionText("Select all prime numbers.")
                .type(mcqMulti)
                .level(easy)
                .build();
        q6.setOptions(List.of(
                new Option(null, q6, "2", true),
                new Option(null, q6, "4", false),
                new Option(null, q6, "5", true),
                new Option(null, q6, "9", false)
        ));

        // Q7: TRUE_FALSE
        Question q7 = Question.builder()
                .questionText("Water boils at 100°C at sea level.")
                .type(trueFalse)
                .level(easy)
                .build();
        q7.setOptions(List.of(
                new Option(null, q7, "True", true),
                new Option(null, q7, "False", false)
        ));

        // Q8: MCQ_SINGLE
        Question q8 = Question.builder()
                .questionText("Which continent is the largest by area?")
                .type(mcqSingle)
                .level(easy)
                .build();
        q8.setOptions(List.of(
                new Option(null, q8, "Africa", false),
                new Option(null, q8, "Asia", true),
                new Option(null, q8, "Europe", false),
                new Option(null, q8, "Antarctica", false)
        ));

        // Q9: MCQ_SINGLE
        Question q9 = Question.builder()
                .questionText("Who wrote 'Romeo and Juliet'?")
                .type(mcqSingle)
                .level(easy)
                .build();
        q9.setOptions(List.of(
                new Option(null, q9, "Charles Dickens", false),
                new Option(null, q9, "William Shakespeare", true),
                new Option(null, q9, "Mark Twain", false),
                new Option(null, q9, "Jane Austen", false)
        ));

        // Q10: MCQ_MULTI
        Question q10 = Question.builder()
                .questionText("Select all mammals.")
                .type(mcqMulti)
                .level(easy)
                .build();
        q10.setOptions(List.of(
                new Option(null, q10, "Dolphin", true),
                new Option(null, q10, "Shark", false),
                new Option(null, q10, "Bat", true),
                new Option(null, q10, "Crocodile", false)
        ));

        // Q11: TRUE_FALSE
        Question q11 = Question.builder()
                .questionText("The chemical symbol for gold is Au.")
                .type(trueFalse)
                .level(medium)
                .build();
        q11.setOptions(List.of(
                new Option(null, q11, "True", true),
                new Option(null, q11, "False", false)
        ));

        // Q12: MCQ_SINGLE
        Question q12 = Question.builder()
                .questionText("What is the capital of Japan?")
                .type(mcqSingle)
                .level(easy)
                .build();
        q12.setOptions(List.of(
                new Option(null, q12, "Kyoto", false),
                new Option(null, q12, "Tokyo", true),
                new Option(null, q12, "Osaka", false),
                new Option(null, q12, "Nagoya", false)
        ));

        questionRepository.saveAll(List.of(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12));
        log.info("✅ Seeded {} questions.", questionRepository.count());
    }
}
