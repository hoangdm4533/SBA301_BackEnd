    package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(7) // Chạy sau QuestionDataInitializer
public class MatrixDataInitializer implements CommandLineRunner {

    private final MatrixRepository matrixRepository;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (matrixRepository.count() > 0) {
            log.info("✅ Matrix data already initialized");
            return;
        }

        log.info("🔄 Initializing matrix data...");

        // Lấy admin user
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        if (adminUser == null) {
            log.warn("⚠️ Cannot initialize matrix data: admin user not found");
            return;
        }

        // Lấy dữ liệu từ database
        List<Lesson> lessons = lessonRepository.findAll();
        List<QuestionType> questionTypes = questionTypeRepository.findAll();
        List<Level> levels = levelRepository.findAll();

        if (lessons.isEmpty() || questionTypes.isEmpty() || levels.isEmpty()) {
            log.warn("⚠️ Cannot initialize matrix data: lessons, question types or levels not found");
            return;
        }

        List<Matrix> matrices = new ArrayList<>();

        // Matrix 1: Level EASY + MCQ_SINGLE + Lesson 1 (10 câu)
        if (lessons.size() >= 1 && questionTypes.size() >= 1 && levels.size() >= 1) {
            Matrix matrix1 = createMatrix(
                    "Ma trận kiểm tra cơ bản - Cấp độ 1",
                    lessons.get(0),
                    questionTypes.get(0),
                    levels.get(0),
                    10,
                    adminUser
            );
            if (matrix1 != null) {
                matrices.add(matrix1);
            }
        }

        // Matrix 2: Level MEDIUM + MCQ_MULTI + Lesson 2 (10 câu)
        if (lessons.size() >= 2 && questionTypes.size() >= 2 && levels.size() >= 2) {
            Matrix matrix2 = createMatrix(
                    "Ma trận kiểm tra trung bình - Cấp độ 2",
                    lessons.get(1),
                    questionTypes.get(1),
                    levels.get(1),
                    10,
                    adminUser
            );
            if (matrix2 != null) {
                matrices.add(matrix2);
            }
        }

        // Matrix 3: Level HARD + TRUE_FALSE + Lesson 3 (10 câu)
        if (lessons.size() >= 3 && questionTypes.size() >= 3 && levels.size() >= 3) {
            Matrix matrix3 = createMatrix(
                    "Ma trận kiểm tra nâng cao - Cấp độ 3",
                    lessons.get(2),
                    questionTypes.get(2),
                    levels.get(2),
                    10,
                    adminUser
            );
            if (matrix3 != null) {
                matrices.add(matrix3);
            }
        }

        // Matrix 4: Hỗn hợp - Bao gồm câu hỏi từ cả 3 level (30 câu)
        Matrix matrix4 = createMixedMatrix(
                "Ma trận tổng hợp - Đa cấp độ",
                lessons,
                questionTypes,
                levels,
                adminUser
        );
        if (matrix4 != null) {
            matrices.add(matrix4);
        }

        if (!matrices.isEmpty()) {
            matrixRepository.saveAll(matrices);
            log.info("✅ Matrix data initialized successfully: {} matrices created", matrices.size());
        } else {
            log.warn("⚠️ No matrix data created - questions not found");
        }
    }

    private Matrix createMatrix(String title, Lesson lesson,
                                QuestionType questionType, Level level, int totalQuestions, User user) {
        // Kiểm tra số câu hỏi có sẵn
        List<Question> questions = questionRepository.findByLevelAndLessonAndType(
                level, lesson, questionType
        );

        if (questions.isEmpty()) {
            log.warn("⚠️ No questions found for level: {}, lesson: {}, type: {}",
                    level.getDifficulty(), lesson.getLessonName(), questionType.getDescription());
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        // Tạo Matrix
        Matrix matrix = Matrix.builder()
                .title(title)
                .totalQuestion(totalQuestions)
                .totalScore(calculateTotalScore(level, totalQuestions))
                .status("ACTIVE")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Tạo MatrixDetail
        MatrixDetail detail = MatrixDetail.builder()
                .totalQuestions(totalQuestions)
                .level(level)
                .questionType(questionType)
                .lesson(lesson)
                .matrix(matrix)
                .createdAt(now)
                .updatedAt(now)
                .build();

        matrix.setDetails(List.of(detail));

        return matrix;
    }

    private Matrix createMixedMatrix(String title,
                                     List<Lesson> lessons, List<QuestionType> questionTypes,
                                     List<Level> levels, User user) {
        LocalDateTime now = LocalDateTime.now();
        List<MatrixDetail> details = new ArrayList<>();
        int totalQuestions = 0;
        double totalScore = 0.0;

        // Tạo MatrixDetail cho mỗi level (10 câu mỗi level)
        for (int i = 0; i < Math.min(3, levels.size()); i++) {
            if (i < lessons.size() && i < questionTypes.size()) {
                Level level = levels.get(i);
                Lesson lesson = lessons.get(i);
                QuestionType qType = questionTypes.get(i);

                List<Question> levelQuestions = questionRepository.findByLevelAndLessonAndType(
                        level, lesson, qType
                );

                if (!levelQuestions.isEmpty()) {
                    int questionsCount = 10; // 10 câu mỗi level
                    totalQuestions += questionsCount;
                    totalScore += calculateTotalScore(level, questionsCount);

                    MatrixDetail detail = MatrixDetail.builder()
                            .totalQuestions(questionsCount)
                            .level(level)
                            .questionType(qType)
                            .lesson(lesson)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    details.add(detail);
                }
            }
        }

        if (details.isEmpty()) {
            log.warn("⚠️ No details created for mixed matrix");
            return null;
        }

        // Tạo Matrix
        Matrix matrix = Matrix.builder()
                .title(title)
                .totalQuestion(totalQuestions)
                .totalScore(totalScore)
                .status("ACTIVE")
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Set matrix cho các details
        details.forEach(detail -> detail.setMatrix(matrix));
        matrix.setDetails(details);

        return matrix;
    }

    private double calculateTotalScore(Level level, int questionCount) {
        // Mỗi level có score khác nhau, nhân với số câu hỏi
        return level.getScore() != null ? level.getScore() * questionCount : 0.0;
    }
}

