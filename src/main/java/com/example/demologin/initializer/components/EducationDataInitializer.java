package com.example.demologin.initializer.components;

import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EducationDataInitializer {

    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final ChapterRepository chapterRepository;
    private final LessonPlanRepository lessonPlanRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeEducation() {
        if (subjectRepository.count() > 0 || gradeRepository.count() > 0) {
            log.info("ℹ️ Education data already present, skipping");
            return;
        }
        User owner = userRepository.findByUsername("admin").orElseGet(() ->
                userRepository.findAll().stream().findFirst().orElse(null)
        );
        if (owner == null) {
            log.warn("⚠️ No user available to assign Subject ownership");
            return;
        }

        // Subject 1: Mathematics with two grades and multiple chapters
        Subject math = Subject.builder()
                .subjectName("Mathematics")
                .user(owner)
                .build();
        math = subjectRepository.save(math);

        Grade g6 = Grade.builder()
                .gradeNumber(6)
                .description("Grade 6 - Basic Math")
                .subject(math)
                .build();
        g6 = gradeRepository.save(g6);

        Grade g7 = Grade.builder()
                .gradeNumber(7)
                .description("Grade 7 - Intermediate Math")
                .subject(math)
                .build();
        g7 = gradeRepository.save(g7);

        Chapter g6c1 = Chapter.builder().name("Numbers and Operations").orderNo(1).grade(g6).build();
        Chapter g6c2 = Chapter.builder().name("Fractions and Decimals").orderNo(2).grade(g6).build();
        Chapter g7c1 = Chapter.builder().name("Algebraic Expressions").orderNo(1).grade(g7).build();
        chapterRepository.saveAll(java.util.List.of(g6c1, g6c2, g7c1));

        LessonPlan lp1 = LessonPlan.builder()
                .title("Grade 6 Math Plan")
                .content("Introduction to numbers, operations, and fractions")
                .filePath(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .grade(g6)
                .build();
        LessonPlan lp2 = LessonPlan.builder()
                .title("Grade 7 Algebra Plan")
                .content("Basics of algebraic expressions and equations")
                .filePath(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .grade(g7)
                .build();
        lessonPlanRepository.saveAll(java.util.List.of(lp1, lp2));

        // Subject 2: Science with one grade and two chapters
        Subject sci = Subject.builder()
                .subjectName("Science")
                .user(owner)
                .build();
        sci = subjectRepository.save(sci);

        Grade g8 = Grade.builder()
                .gradeNumber(8)
                .description("Grade 8 - General Science")
                .subject(sci)
                .build();
        g8 = gradeRepository.save(g8);

        Chapter g8c1 = Chapter.builder().name("Cells and Organisms").orderNo(1).grade(g8).build();
        Chapter g8c2 = Chapter.builder().name("Forces and Motion").orderNo(2).grade(g8).build();
        chapterRepository.saveAll(java.util.List.of(g8c1, g8c2));

        LessonPlan lp3 = LessonPlan.builder()
                .title("Grade 8 Science Plan")
                .content("Overview of biology and physics basics")
                .filePath(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .grade(g8)
                .build();
        lessonPlanRepository.save(lp3);

        log.info("✅ Seeded education data: subjects={}, grades={}, chapters added, lesson plans added",
                subjectRepository.count(), gradeRepository.count());
    }
}
