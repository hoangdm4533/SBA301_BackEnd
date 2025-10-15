package com.example.demologin.initializer.components;

import com.example.demologin.entity.Exam;
import com.example.demologin.entity.Matrix;
import com.example.demologin.entity.User;
import com.example.demologin.repository.ExamRepository;
import com.example.demologin.repository.MatrixRepository;
import com.example.demologin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Exam Data Initializer
 * 
 * Responsible for creating sample exam data for testing and development.
 * This runs after DefaultUserInitializer and MatrixDataInitializer since exams depend on users and matrices.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamDataInitializer {

    private final ExamRepository examRepository;
    private final MatrixRepository matrixRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeExams() {
        log.info("📝 Initializing exam data...");
        
        if (examRepository.count() > 0) {
            log.info("ℹ️ Exams already exist, skipping exam initialization");
            return;
        }

        createSampleExams();
        
        log.info("✅ Successfully initialized {} exams", examRepository.count());
    }

    private void createSampleExams() {
        log.debug("📝 Creating sample exams...");

        // Get a user for creating exams
        Optional<User> adminUser = userRepository.findByUsername("admin");
        if (adminUser.isEmpty()) {
            log.warn("⚠️ Admin user not found, skipping exam creation");
            return;
        }

        // Get first matrix if exists
        Optional<Matrix> sampleMatrix = matrixRepository.findAll().stream().findFirst();
        
        List<Exam> exams = new ArrayList<>();

        // Create Draft Exam
        exams.add(createExam(
            "Kiểm tra Toán học lớp 10 - Chương 1",
            "Bài kiểm tra về Đại số và Giải tích cơ bản",
            "DRAFT",
            sampleMatrix.orElse(null)
        ));

        // Create Published Exam
        exams.add(createExam(
            "Kiểm tra Vật lý lớp 11 - Điện học",
            "Bài kiểm tra về Điện trường và Dòng điện",
            "PUBLISHED",
            sampleMatrix.orElse(null)
        ));

        // Create Another Draft Exam
        exams.add(createExam(
            "Kiểm tra Hóa học lớp 12 - Hữu cơ",
            "Bài kiểm tra về Hợp chất hữu cơ",
            "DRAFT",
            sampleMatrix.orElse(null)
        ));

        // Create Archived Exam
        exams.add(createExam(
            "Kiểm tra Sinh học lớp 10 - Tế bào",
            "Bài kiểm tra về Cấu trúc tế bào",
            "ARCHIVED",
            sampleMatrix.orElse(null)
        ));

        // Create Another Published Exam
        exams.add(createExam(
            "Kiểm tra Tiếng Anh lớp 11 - Grammar",
            "Bài kiểm tra về Ngữ pháp tiếng Anh",
            "PUBLISHED",
            sampleMatrix.orElse(null)
        ));

        examRepository.saveAll(exams);
        log.debug("✅ Created {} sample exams", exams.size());
    }

    private Exam createExam(String title, String description, String status, Matrix matrix) {
        LocalDateTime now = LocalDateTime.now();
        
        return Exam.builder()
                .title(title)
                .description(description)
                .status(status)
                .matrix(matrix)
                .createdAt(now)
                .updatedAt(now)
                .examQuestions(new ArrayList<>())
                .build();
    }
}
