package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptDataInitializer {

    ExamAttemptRepository examAttemptRepository;
    ExamRepository examRepository;
    UserRepository userRepository;

    @Transactional
    public void initializeAttempts() {
        if (examAttemptRepository.count() > 0) {
            return;
        }
        Optional<User> memberOpt = userRepository.findByUsername("member");
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        if (memberOpt.isEmpty() && adminOpt.isEmpty()) {
            return;
        }
        User member = memberOpt.orElse(null);
        User admin = adminOpt.orElse(null);

        Exam publishedExam = examRepository.findAll().stream()
                .filter(e -> "PUBLISHED".equalsIgnoreCase(e.getStatus()))
                .findFirst().orElse(null);
        if (publishedExam == null) {
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
    }
}
