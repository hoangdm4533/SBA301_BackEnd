package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Exam Template Data Initializer
 *
 * Responsible for creating initial exam template data for the system.
 * This creates sample exam templates for each level with questions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamTemplateDataInitializer {

    private final ExamTemplateRepository examTemplateRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeExamTemplates() {
        log.info("📝 Initializing exam templates...");

        if (examTemplateRepository.count() > 0) {
            log.info("ℹ️ Exam templates already exist, skipping initialization");
            return;
        }

        // Get admin user and teacher user
        User adminUser = userRepository.findByUsername("admin").orElse(null);
        User teacherUser = userRepository.findByEmail("teacher@example.com").orElse(adminUser);

        if (adminUser == null) {
            log.warn("⚠️ Admin user not found, exam templates will be created without creator");
        }

        // Get levels
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            log.warn("⚠️ No levels found, skipping exam template initialization");
            return;
        }

        // Create exam templates for each level
        for (Level level : levels) {
            createExamTemplatesForLevel(level, teacherUser, adminUser);
        }

        log.info("✅ Successfully initialized exam templates for {} levels", levels.size());
    }

    private void createExamTemplatesForLevel(Level level, User teacher, User admin) {
        // Create basic exam template
        ExamTemplate basicTemplate = ExamTemplate.builder()
                .title(level.getName() + " - Basic Test")
                .description("Bài kiểm tra cơ bản cho level " + level.getName())
                .level(level)
                .difficulty(level.getDifficulty())
                .status("PUBLISHED")
                .duration(60) // 60 minutes
                .totalQuestions(10)
                .totalPoints(100.0)
                .createdBy(teacher)
                .updatedBy(teacher)
                .approvedBy(admin)
                .build();

        // Create advanced exam template
        ExamTemplate advancedTemplate = ExamTemplate.builder()
                .title(level.getName() + " - Advanced Test")
                .description("Bài kiểm tra nâng cao cho level " + level.getName())
                .level(level)
                .difficulty(level.getDifficulty())
                .status("PUBLISHED")
                .duration(90) // 90 minutes
                .totalQuestions(15)
                .totalPoints(150.0)
                .createdBy(teacher)
                .updatedBy(teacher)
                .approvedBy(admin)
                .build();

        // Create practice exam template
        ExamTemplate practiceTemplate = ExamTemplate.builder()
                .title(level.getName() + " - Practice Test")
                .description("Bài luyện tập cho level " + level.getName())
                .level(level)
                .difficulty(level.getDifficulty())
                .status("PUBLISHED")
                .duration(45) // 45 minutes
                .totalQuestions(8)
                .totalPoints(80.0)
                .createdBy(teacher)
                .updatedBy(teacher)
                .approvedBy(admin)
                .build();

        // Create draft template
        ExamTemplate draftTemplate = ExamTemplate.builder()
                .title(level.getName() + " - Draft Test")
                .description("Bài kiểm tra đang soạn thảo cho level " + level.getName())
                .level(level)
                .difficulty(level.getDifficulty())
                .status("DRAFT")
                .duration(60)
                .totalQuestions(0)
                .totalPoints(0.0)
                .createdBy(teacher)
                .updatedBy(teacher)
                .build();

        // Save templates
        List<ExamTemplate> templates = Arrays.asList(basicTemplate, advancedTemplate, practiceTemplate, draftTemplate);
        examTemplateRepository.saveAll(templates);

        // Note: ExamQuestion relationships will be created by ExamQuestionDataInitializer
        log.debug("Created {} exam templates for level: {}", templates.size(), level.getName());
    }
}