package com.example.demologin.initializer.components;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demologin.entity.Exam;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import com.example.demologin.entity.User;
import com.example.demologin.repository.ExamRepository;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.OptionRepository;
import com.example.demologin.repository.QuestionRepository;
import com.example.demologin.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Exam Data Initializer
 * 
 * Responsible for creating sample exam data including questions and options.
 * This must run after EducationDataInitializer and DefaultUserInitializer
 * since it depends on grades and teachers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExamDataInitializer {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeExamData() {
        log.info("📝 Initializing exam data (questions, options, exams)...");
        
        initializeQuestions();
        initializeExams();
        
        log.info("✅ Successfully initialized exam data");
    }

    private void initializeQuestions() {
        log.debug("❓ Creating sample questions...");
        
        if (questionRepository.count() > 0) {
            log.info("ℹ️ Questions already exist, skipping question initialization");
            return;
        }

        List<Grade> grades = gradeRepository.findAll();
        Optional<User> adminUser = userRepository.findByUsername("admin"); // Sử dụng admin làm teacher mẫu
        
        if (grades.isEmpty() || adminUser.isEmpty()) {
            log.warn("⚠️ No grades or admin user found, cannot create questions");
            return;
        }

        User teacher = adminUser.get(); // Sử dụng admin làm teacher
        LocalDateTime now = LocalDateTime.now();

        // Tạo câu hỏi cho từng khối lớp
        for (Grade grade : grades) {
            createQuestionsForGrade(grade, teacher, now);
        }
        
        log.debug("✅ Created {} questions", questionRepository.count());
    }

    private void createQuestionsForGrade(Grade grade, User teacher, LocalDateTime now) {
        // Câu hỏi Toán học - Beginner
        Question mathQuestion1 = Question.builder()
            .teacher(teacher)
            .questionText("Tính: 15 + 27 = ?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Beginner")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(mathQuestion1);
        createOptionsForQuestion(mathQuestion1, List.of("42", "41", "43", "40"), 0);

        Question mathQuestion2 = Question.builder()
            .teacher(teacher)
            .questionText("Một hình chữ nhật có chiều dài 8cm, chiều rộng 5cm. Tính diện tích?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Intermediate")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(mathQuestion2);
        createOptionsForQuestion(mathQuestion2, List.of("40 cm²", "39 cm²", "41 cm²", "38 cm²"), 0);

        // Câu hỏi Toán nâng cao - Advanced
        Question mathQuestion3 = Question.builder()
            .teacher(teacher)
            .questionText("Giải phương trình: 2x² + 5x - 3 = 0")
            .type("MULTIPLE_CHOICE")
            .difficulty("Advanced")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(mathQuestion3);
        createOptionsForQuestion(mathQuestion3, List.of("x = 1/2 hoặc x = -3", "x = 1 hoặc x = -3", "x = -1/2 hoặc x = 3", "x = 2 hoặc x = -1"), 0);

        // Câu hỏi Toán chuyên sâu - Expert
        Question mathQuestion4 = Question.builder()
            .teacher(teacher)
            .questionText("Tính tích phân: ∫(0 to π) sin(x)dx")
            .type("MULTIPLE_CHOICE")
            .difficulty("Expert")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(mathQuestion4);
        createOptionsForQuestion(mathQuestion4, List.of("2", "1", "π", "0"), 0);

        // Câu hỏi Tiếng Việt - Beginner
        Question vietnameseQuestion1 = Question.builder()
            .teacher(teacher)
            .questionText("Từ nào sau đây là danh từ?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Beginner")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(vietnameseQuestion1);
        createOptionsForQuestion(vietnameseQuestion1, List.of("sách", "đọc", "nhanh", "đẹp"), 0);

        Question vietnameseQuestion2 = Question.builder()
            .teacher(teacher)
            .questionText("Câu nào sau đây là câu cảm thán?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Intermediate")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(vietnameseQuestion2);
        createOptionsForQuestion(vietnameseQuestion2, 
            List.of("Trời hôm nay đẹp quá!", "Hôm nay trời đẹp.", "Trời có đẹp không?", "Hãy nhìn trời."), 0);

        // Câu hỏi Văn học - Advanced
        Question vietnameseQuestion3 = Question.builder()
            .teacher(teacher)
            .questionText("Tác phẩm 'Truyện Kiều' của Nguyễn Du thuộc thể loại nào?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Advanced")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(vietnameseQuestion3);
        createOptionsForQuestion(vietnameseQuestion3, List.of("Truyện thơ", "Tiểu thuyết", "Kịch", "Tản văn"), 0);

        // Câu hỏi Ngữ văn chuyên sâu - Expert
        Question vietnameseQuestion4 = Question.builder()
            .teacher(teacher)
            .questionText("Phân tích tác dụng của việc sử dụng điệp từ trong thơ Nguyễn Du")
            .type("ESSAY")
            .difficulty("Expert")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(vietnameseQuestion4);
        // Câu hỏi ESSAY không cần tạo options

        // Câu hỏi Khoa học - Beginner
        Question scienceQuestion1 = Question.builder()
            .teacher(teacher)
            .questionText("Nước sôi ở nhiệt độ bao nhiêu độ C?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Beginner")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(scienceQuestion1);
        createOptionsForQuestion(scienceQuestion1, List.of("100°C", "90°C", "110°C", "80°C"), 0);

        Question scienceQuestion2 = Question.builder()
            .teacher(teacher)
            .questionText("Cơ quan nào của cây làm nhiệm vụ quang hợp?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Intermediate")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(scienceQuestion2);
        createOptionsForQuestion(scienceQuestion2, List.of("Lá", "Thân", "Rễ", "Hoa"), 0);

        // Câu hỏi Vật lý - Advanced
        Question scienceQuestion3 = Question.builder()
            .teacher(teacher)
            .questionText("Tần số dao động của con lắc đơn phụ thuộc vào yếu tố nào?")
            .type("MULTIPLE_CHOICE")
            .difficulty("Advanced")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(scienceQuestion3);
        createOptionsForQuestion(scienceQuestion3, List.of("Chiều dài dây và gia tốc trọng trường", "Khối lượng và chiều dài", "Biên độ dao động", "Nhiệt độ môi trường"), 0);

        // Câu hỏi Hóa học chuyên sâu - Expert  
        Question scienceQuestion4 = Question.builder()
            .teacher(teacher)
            .questionText("Viết phương trình cân bằng và giải thích cơ chế phản ứng SN2")
            .type("ESSAY")
            .difficulty("Expert")
            .createdAt(now)
            .updatedAt(now)
            .grades(List.of(grade))
            .build();
        
        questionRepository.save(scienceQuestion4);
        // Câu hỏi ESSAY không cần tạo options
    }

    private void createOptionsForQuestion(Question question, List<String> optionTexts, int correctIndex) {
        for (int i = 0; i < optionTexts.size(); i++) {
            Option option = Option.builder()
                .question(question)
                .optionText(optionTexts.get(i))
                .isCorrect(i == correctIndex)
                .build();
            
            optionRepository.save(option);
        }
    }

    private void initializeExams() {
        log.debug("📋 Creating sample exams...");
        
        if (examRepository.count() > 0) {
            log.info("ℹ️ Exams already exist, skipping exam initialization");
            return;
        }

        List<Grade> grades = gradeRepository.findAll();
        Optional<User> adminUser = userRepository.findByUsername("admin");
        
        if (grades.isEmpty() || adminUser.isEmpty()) {
            log.warn("⚠️ No grades or admin user found, cannot create exams");
            return;
        }

        User teacher = adminUser.get();
        LocalDateTime now = LocalDateTime.now();

        // Tạo bài kiểm tra cho từng khối lớp
        for (Grade grade : grades) {
            createExamsForGrade(grade, teacher, now);
        }
        
        log.debug("✅ Created {} exams", examRepository.count());
    }

    private void createExamsForGrade(Grade grade, User teacher, LocalDateTime now) {
        // Lấy tất cả câu hỏi và filter theo grade
        List<Question> allQuestions = questionRepository.findAll();
        List<Question> gradeQuestions = allQuestions.stream()
            .filter(q -> q.getGrades().contains(grade))
            .toList();
        
        if (gradeQuestions.isEmpty()) {
            log.warn("⚠️ No questions found for grade {}, cannot create exams", grade.getName());
            return;
        }

        // Tạo bài kiểm tra Toán học
        Exam mathExam = Exam.builder()
            .teacher(teacher)
            .grade(grade)
            .title("Kiểm tra Toán học - " + grade.getName())
            .description("Bài kiểm tra môn Toán học dành cho " + grade.getName())
            .difficulty("MEDIUM")
            .status("PUBLISHED")
            .approvedBy(teacher)
            .createdAt(now)
            .updatedAt(now)
            .questions(gradeQuestions.subList(0, Math.min(2, gradeQuestions.size()))) // Lấy 2 câu hỏi đầu
            .build();
        
        examRepository.save(mathExam);

        // Tạo bài kiểm tra Tiếng Việt
        if (gradeQuestions.size() >= 4) {
            Exam vietnameseExam = Exam.builder()
                .teacher(teacher)
                .grade(grade)
                .title("Kiểm tra Tiếng Việt - " + grade.getName())
                .description("Bài kiểm tra môn Tiếng Việt dành cho " + grade.getName())
                .difficulty("MEDIUM")
                .status("PUBLISHED")
                .approvedBy(teacher)
                .createdAt(now)
                .updatedAt(now)
                .questions(gradeQuestions.subList(2, 4)) // Lấy 2 câu hỏi tiếp theo
                .build();
            
            examRepository.save(vietnameseExam);
        }

        // Tạo bài kiểm tra Khoa học
        if (gradeQuestions.size() >= 6) {
            Exam scienceExam = Exam.builder()
                .teacher(teacher)
                .grade(grade)
                .title("Kiểm tra Khoa học - " + grade.getName())
                .description("Bài kiểm tra môn Khoa học dành cho " + grade.getName())
                .difficulty("MEDIUM")
                .status("PUBLISHED")
                .approvedBy(teacher)
                .createdAt(now)
                .updatedAt(now)
                .questions(gradeQuestions.subList(4, 6)) // Lấy 2 câu hỏi cuối
                .build();
            
            examRepository.save(scienceExam);
        }
    }
}