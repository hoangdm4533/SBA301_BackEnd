package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttemptDataInitializer {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeAttempts() {
        if (examAttemptRepository.count() > 0) {
            log.info("ℹ️ Exam attempts already exist, skipping");
            return;
        }
        Optional<User> memberOpt = userRepository.findByUsername("member");
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        if (memberOpt.isEmpty() && adminOpt.isEmpty()) {
            log.warn("⚠️ No 'member' or 'admin' user found, cannot seed attempts");
            return;
        }
        User member = memberOpt.orElse(null);
        User admin = adminOpt.orElse(null);

        Exam publishedExam = examRepository.findAll().stream()
                .filter(e -> "PUBLISHED".equalsIgnoreCase(e.getStatus()))
                .findFirst().orElse(null);
        if (publishedExam == null) {
            log.warn("⚠️ No published exam found, cannot seed attempts");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        // Attempt 1: member ungraded
        if (member != null) {
            ExamAttempt a1 = ExamAttempt.builder()
                    .exam(publishedExam)
                    .user(member)
                    .startedAt(now.minusMinutes(25))
                    .finishedAt(now.minusMinutes(10))
                    .gradedBy("admin")
                    .score(null)
                    .build();
            examAttemptRepository.save(a1);

            // Attempt 2: member graded
            ExamAttempt a2 = ExamAttempt.builder()
                    .exam(publishedExam)
                    .user(member)
                    .startedAt(now.minusDays(1).minusMinutes(40))
                    .finishedAt(now.minusDays(1).minusMinutes(20))
                    .gradedBy("admin")
                    .score(2.0)
                    .build();
            examAttemptRepository.save(a2);
        }

        // Attempt 3: admin graded (for testing admin taking exam)
        if (admin != null) {
            ExamAttempt a3 = ExamAttempt.builder()
                    .exam(publishedExam)
                    .user(admin)
                    .startedAt(now.minusDays(2).minusMinutes(15))
                    .finishedAt(now.minusDays(2))
                    .gradedBy("admin")
                    .score(3.0)
                    .build();
            examAttemptRepository.save(a3);
        }

        log.info("✅ Seeded sample exam attempts for available users on published exam '{}'", publishedExam.getTitle());
    }
}
