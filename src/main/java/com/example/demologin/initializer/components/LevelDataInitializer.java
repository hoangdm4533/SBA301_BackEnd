package com.example.demologin.initializer.components;

import com.example.demologin.entity.Level;
import com.example.demologin.repository.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LevelDataInitializer {
    private final LevelRepository levelRepository;

    @Transactional
    public void initializeLevels() {
        if (levelRepository.count() > 0) {
            return;
        }
        levelRepository.saveAll(List.of(
                Level.builder().description("EASY").score(1.0).build(),
                Level.builder().description("MEDIUM").score(2.0).build(),
                Level.builder().description("HARD").score(3.0).build()
        ));
    }
}
