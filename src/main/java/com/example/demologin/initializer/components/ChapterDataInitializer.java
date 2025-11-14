package com.example.demologin.initializer.components;

import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.Subject;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.GradeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterDataInitializer {

    ChapterRepository chapterRepository;
    GradeRepository gradeRepository;

    public void initChapter() {
        if (chapterRepository.count() == 0) {
            Grade grade1 = gradeRepository.findById(1L).orElse(null);
            Grade grade2 = gradeRepository.findById(2L).orElse(null);

            if (grade1 != null) {
                Chapter chapter1 = Chapter.builder()
                        .name("Numbers and Counting")
                        .orderNo(1)
                        .grade(grade1)
                        .build();

                Chapter chapter2 = Chapter.builder()
                        .name("Addition and Subtraction")
                        .orderNo(2)
                        .grade(grade1)
                        .build();

                chapterRepository.save(chapter1);
                chapterRepository.save(chapter2);
            }

            if (grade2 != null) {
                Chapter chapter3 = Chapter.builder()
                        .name("Introduction to Motion")
                        .orderNo(1)
                        .grade(grade2)
                        .build();

                Chapter chapter4 = Chapter.builder()
                        .name("Forces and Energy")
                        .orderNo(2)
                        .grade(grade2)
                        .build();

                chapterRepository.save(chapter3);
                chapterRepository.save(chapter4);
            }
        }
    }
}
