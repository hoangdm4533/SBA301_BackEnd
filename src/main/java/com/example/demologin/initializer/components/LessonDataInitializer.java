package com.example.demologin.initializer.components;

import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Lesson;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LessonDataInitializer {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void initializeLessons() {
        if (lessonRepository.count() > 0) {
            log.info("ℹ️ Lessons already exist, skip seeding.");
            return;
        }

        List<Chapter> chapters = chapterRepository.findAll();
        if (chapters.isEmpty()) {
            throw new IllegalStateException("⚠️ No chapters found in database. Please seed Chapter data first.");
        }

        List<Lesson> lessons = new ArrayList<>();

        for (Chapter chapter : chapters) {
            Long chId = chapter.getId();
            switch (chId.intValue()) {
                case 1 -> {
                    lessons.add(Lesson.builder().lessonName("Introduction to Mathematics").chapter(chapter).build());
                    lessons.add(Lesson.builder().lessonName("Basic Numbers and Counting").chapter(chapter).build());
                }
                case 2 -> {
                    lessons.add(Lesson.builder().lessonName("Geometry Fundamentals").chapter(chapter).build());
                    lessons.add(Lesson.builder().lessonName("Shapes and Angles").chapter(chapter).build());
                }
                case 3 -> {
                    lessons.add(Lesson.builder().lessonName("Algebra Basics").chapter(chapter).build());
                    lessons.add(Lesson.builder().lessonName("Equations and Variables").chapter(chapter).build());
                }
                case 4 -> {
                    lessons.add(Lesson.builder().lessonName("Measurement and Units").chapter(chapter).build());
                    lessons.add(Lesson.builder().lessonName("Time and Distance").chapter(chapter).build());
                }
                case 5 -> {
                    lessons.add(Lesson.builder().lessonName("Data and Statistics").chapter(chapter).build());
                    lessons.add(Lesson.builder().lessonName("Charts and Graphs").chapter(chapter).build());
                }
                default -> {
                    lessons.add(Lesson.builder().lessonName("General Concepts for Chapter " + chId).chapter(chapter).build());
                }
            }
        }

        lessonRepository.saveAll(lessons);
        log.info("✅ Seeded {} lessons for {} chapters.", lessonRepository.count(), chapters.size());
    }
}
