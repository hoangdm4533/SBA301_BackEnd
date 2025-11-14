package com.example.demologin.initializer.components;

import com.example.demologin.entity.QuestionType;
import com.example.demologin.repository.QuestionTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionTypeDataInitializer {
    QuestionTypeRepository questionTypeRepository;

    @Transactional
    public void initializeQuestionTypes() {
        List<String> types = List.of("MCQ_SINGLE", "MCQ_MULTI", "TRUE_FALSE");

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
    }
}
