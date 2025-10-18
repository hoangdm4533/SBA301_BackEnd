package com.example.demologin.initializer.components;

import com.example.demologin.entity.QuestionType;
import com.example.demologin.repository.QuestionTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionTypeDataInitializer {
    private final QuestionTypeRepository questionTypeRepository;

    @Transactional
    public void initializeQuestionTypes() {
        // Các loại phổ biến: đơn chọn, nhiều chọn, đúng/sai, tự luận ngắn
        List<String> types = List.of("MCQ_SINGLE", "MCQ_MULTI", "TRUE_FALSE", "SHORT_ANSWER");

        int created = 0;
        for (String t : types) {
            boolean exists = questionTypeRepository.findByDescriptionIgnoreCase(t).isPresent();
            if (!exists) {
                questionTypeRepository.save(
                        QuestionType.builder().description(t).build()
                );
                created++;
            }
        }
        log.info("✅ QuestionType init: added {} new types (total: {})", created, questionTypeRepository.count());
    }
}
