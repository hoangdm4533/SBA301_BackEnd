package com.example.demologin.initializer;

import com.example.demologin.initializer.components.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Data Initializer - Orchestrates all initialization processes
 * 
 * This class coordinates the execution of all data initialization components
 * in the correct order to ensure system integrity and proper dependencies.
 * 
 * Execution Order:
 * 1. PermissionRoleInitializer - Creates permissions and roles
 * 2. DefaultUserInitializer - Creates default users with assigned roles
 * 3. PlanDataInitializer - Creates subscription plans
 * 4. EducationDataInitializer - Creates grades, classes, and lesson plans
 * 5. ChapterDataInitializer - Creates chapters for lesson plans
 * 6. LevelDataInitializer - Creates exam levels
 * 7. ExamDataInitializer - Creates questions, options, and exams
 * 8. ExamTemplateDataInitializer - Creates exam templates with questions
 * 9. ExamQuestionDataInitializer - Links questions to exam templates
 * 10. Future initializers can be added here with proper ordering
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Ensure this runs first among all CommandLineRunners
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MainDataInitializer implements CommandLineRunner {

//    PermissionRoleInitializer permissionRoleInitializer;
//    DefaultUserInitializer defaultUserInitializer;

    QuestionTypeDataInitializer questionTypeDataInitializer;
    LevelDataInitializer levelDataInitializer;
    QuestionDataInitializer questionDataInitializer;
    PlanDataInitializer planDataInitializer;
    EducationDataInitializer educationDataInitializer;
    AttemptDataInitializer attemptDataInitializer;
    GradeDataInitializer gradeDataInitializer;
    ChapterDataInitializer chapterDataInitializer;

    ExamDataInitializer examDataInitializer;
    UserInitializer userInitializer;
    LessonDataInitializer lessonDataInitializer;
    MatrixDataInitializer matrixDataInitializer;
    @Override
    public void run(String... args) throws Exception {
        
        try {
//            permissionRoleInitializer.initializePermissionsAndRoles();
//            defaultUserInitializer.initializeDefaultUsers();
            gradeDataInitializer.initGrade();
            chapterDataInitializer.initChapter();
            planDataInitializer.initializePlansAndSubscriptions();
            educationDataInitializer.initializeEducation();
            lessonDataInitializer.initializeLessons();
            questionTypeDataInitializer.initializeQuestionTypes();
            levelDataInitializer.initializeLevels();
            questionDataInitializer.initializeQuestions();
            matrixDataInitializer.run();
            examDataInitializer.initializeExams();
            attemptDataInitializer.initializeAttempts();
            userInitializer.initializeUsers();
        } catch (Exception e) {
            throw e; // Re-throw to prevent application startup with incomplete data
        }
    }
}
