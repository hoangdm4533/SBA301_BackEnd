package com.example.demologin.initializer.components;

import com.example.demologin.entity.Level;
import com.example.demologin.entity.User;
import com.example.demologin.repository.LevelRepository;
import com.example.demologin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Level Data Initializer
 *
 * Responsible for creating initial level data for the exam system.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LevelDataInitializer {

    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeLevels() {
        log.info("📊 Initializing exam levels...");

        if (levelRepository.count() > 0) {
            log.info("ℹ️ Levels already exist, skipping initialization");
            return;
        }

        // Get admin user for created_by
        User adminUser = userRepository.findByUsername("admin")
                .orElse(null);

        if (adminUser == null) {
            log.warn("⚠️ Admin user not found, levels will be created without creator");
        }

        List<Level> levels = Arrays.asList(
                Level.builder()
                        .name("Beginner")
                        .description("Dành cho người mới bắt đầu học")
                        .difficulty("EASY")
                        .status("ACTIVE")
                        .minScore(0)
                        .maxScore(40)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build(),

                Level.builder()
                        .name("Elementary")
                        .description("Trình độ tiểu học")
                        .difficulty("EASY")
                        .status("ACTIVE")
                        .minScore(41)
                        .maxScore(60)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build(),

                Level.builder()
                        .name("Intermediate")
                        .description("Trình độ trung bình")
                        .difficulty("MEDIUM")
                        .status("ACTIVE")
                        .minScore(61)
                        .maxScore(75)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build(),

                Level.builder()
                        .name("Upper Intermediate")
                        .description("Trình độ trung bình khá")
                        .difficulty("MEDIUM")
                        .status("ACTIVE")
                        .minScore(76)
                        .maxScore(85)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build(),

                Level.builder()
                        .name("Advanced")
                        .description("Trình độ nâng cao")
                        .difficulty("HARD")
                        .status("ACTIVE")
                        .minScore(86)
                        .maxScore(95)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build(),

                Level.builder()
                        .name("Expert")
                        .description("Trình độ chuyên gia")
                        .difficulty("HARD")
                        .status("ACTIVE")
                        .minScore(96)
                        .maxScore(100)
                        .createdBy(adminUser)
                        .updatedBy(adminUser)
                        .build()
        );

        levelRepository.saveAll(levels);

        log.info("✅ Successfully initialized {} levels", levels.size());
    }
}