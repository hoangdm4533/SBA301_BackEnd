package com.example.demologin.service;

import com.example.demologin.entity.ExamAttempt;
import com.example.demologin.repository.ExamAttemptRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service tự động nộp bài thi khi hết thời gian
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExamAutoSubmitScheduler {

    private final ExamAttemptRepository examAttemptRepository;

    /**
     * Chạy mỗi 1 phút để kiểm tra và tự động nộp các bài thi đã hết hạn
     */
    @Scheduled(cron = "0 * * * * ?") // Chạy vào giây 0 của mỗi phút
    @Transactional
    public void autoSubmitExpiredAttempts() {
        LocalDateTime now = LocalDateTime.now();

        // Tìm các attempt đã hết hạn nhưng chưa nộp
        List<ExamAttempt> expiredAttempts = examAttemptRepository
                .findByExpiresAtBeforeAndFinishedAtIsNull(now);

        if (expiredAttempts.isEmpty()) {
            return;
        }

        log.info("Found {} expired attempts to auto-submit", expiredAttempts.size());

        for (ExamAttempt attempt : expiredAttempts) {
            try {
                autoSubmitAttempt(attempt, now);
                log.info("Auto-submitted attempt {} for user {}",
                    attempt.getId(),
                    attempt.getUser().getUserId());
            } catch (Exception e) {
                log.error("Failed to auto-submit attempt {}: {}",
                    attempt.getId(),
                    e.getMessage(), e);
            }
        }
    }

    /**
     * Tự động nộp bài và tính điểm 0 (vì không có câu trả lời)
     */
    private void autoSubmitAttempt(ExamAttempt attempt, LocalDateTime finishedAt) {
        // Đánh dấu đã nộp với điểm 0
        attempt.setFinishedAt(finishedAt);
        attempt.setScore(0.0);
        attempt.setGradedBy("SYSTEM_AUTO_SUBMIT");

        examAttemptRepository.save(attempt);
    }
}

