package com.example.demologin.initializer.components;

import com.example.demologin.entity.Level;
import com.example.demologin.repository.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LevelDataInitializer {
    LevelRepository levelRepository;

    @Transactional
    public void initializeLevels() {
        if (levelRepository.count() > 0) {
            return;
        }
        levelRepository.saveAll(List.of(
                Level.builder().difficulty("EASY").score(1.0).build(),
                Level.builder().difficulty("MEDIUM").score(2.0).build(),
                Level.builder().difficulty("HARD").score(3.0).build()
        ));
    }
}
