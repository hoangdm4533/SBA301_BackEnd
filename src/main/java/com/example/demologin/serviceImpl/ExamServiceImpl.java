package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.exam.ExamRequest;
import com.example.demologin.dto.request.exam.AddQuestionToExamRequest;
import com.example.demologin.dto.response.ExamAttemptRow;
import com.example.demologin.dto.response.ExamResponse;
import com.example.demologin.dto.response.ExamQuestionResponse;
import com.example.demologin.entity.*;
import com.example.demologin.exception.exceptions.BadRequestException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.ConflictException;
import com.example.demologin.repository.*;
import com.example.demologin.service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExamServiceImpl implements ExamService {
    
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final UserRepository userRepository;
    private final MatrixRepository matrixRepository;

    private ExamResponse mapToResponse(Exam exam) {
        List<ExamQuestionResponse> questions = examQuestionRepository.findByExam(exam)
                .stream()
                .map(this::mapExamQuestionToResponse)
                .collect(Collectors.toList());

        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .status(exam.getStatus())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .questions(questions)
                .build();
    }

    private ExamQuestionResponse mapExamQuestionToResponse(ExamQuestion examQuestion) {
        Question q = examQuestion.getQuestion();

        String questionTypeDesc = null;
        if (q != null && q.getType() != null) {
            questionTypeDesc = q.getType().getDescription();
        }

        return ExamQuestionResponse.builder()
                .id(examQuestion.getId())
                .examId(examQuestion.getExam().getId())
                .questionId(q != null ? q.getId() : null)
                .questionText(q != null ? q.getQuestionText() : null)
                .questionType(questionTypeDesc)   // <<== đổi sang String mô tả
                .score(examQuestion.getScore())
                .build();
    }

    @Override
    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        try{
            // Kiểm tra trùng lặp title
            if (examRepository.existsByTitle(request.getTitle())) {
                throw new ConflictException("Đã tồn tại exam với tiêu đề '" + request.getTitle() + "'");
            }
            Matrix matrix = matrixRepository.findById(request.getMatrixId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy matrix" + request.getMatrixId()));

            Exam exam = Exam.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .matrix(matrix)
                    .build();

            Exam saved = examRepository.save(exam);
            return mapToResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + id));
        return mapToResponse(exam);
    }

    @Override
    public List<ExamResponse> getAllExams() {
        return examRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ExamResponse> getAllExams(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return examRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + id));

        // Kiểm tra trùng lặp title (ngoại trừ chính nó)
        if (!exam.getTitle().equals(request.getTitle()) && 
            examRepository.existsByTitle(request.getTitle())) {
            throw new ConflictException("Đã tồn tại exam với tiêu đề '" + request.getTitle() + "'");
        }

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStatus(request.getStatus());
        exam.setUpdatedAt(LocalDateTime.now());

        Exam updated = examRepository.save(exam);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + id));
        
        // Xóa tất cả câu hỏi trong exam trước
        examQuestionRepository.deleteByExam(exam);
        examRepository.delete(exam);
    }

    @Override
    public Page<ExamResponse> getExamsByStatus(String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return examRepository.findByStatus(status, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExamResponse> searchExams(String keyword, Pageable pageable) {
        return examRepository.findByTitleContainingOrDescriptionContaining(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ExamQuestionResponse addQuestionToExam(Long examId, AddQuestionToExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + examId));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + request.getQuestionId()));

        // Kiểm tra trùng câu hỏi
        if (examQuestionRepository.existsByExamAndQuestion(exam, question)) {
            throw new ConflictException("Question đã tồn tại trong exam này");
        }

        // Lấy Level của question
        Level level = question.getLevel();
        if (level == null) {
            throw new ConflictException("Question không có level hợp lệ");
        }

        // Lấy Matrix từ Exam
        Matrix matrix = exam.getMatrix();
        if (matrix == null) {
            throw new ConflictException("Exam chưa được gắn Matrix");
        }

        // Tìm MatrixDetail tương ứng với Level đó
        MatrixDetail matrixDetail = matrix.getDetails().stream()
                .filter(md -> md.getLevel().getId().equals(level.getId()))
                .findFirst()
                .orElseThrow(() -> new ConflictException(
                        "Không tìm thấy MatrixDetail cho Level " + level.getDescription()));

        // Đếm số lượng câu hỏi trong exam theo level
        long countByLevel = examQuestionRepository.countByExamAndQuestion_Level(exam, level);

        if (countByLevel >= matrixDetail.getTotalQuestions()) {
            throw new ConflictException(String.format(
                    "Số câu hỏi Level '%s' đã đạt giới hạn (%d/%d)",
                    level.getDescription(), countByLevel, matrixDetail.getTotalQuestions()
            ));
        }

        // Tạo ExamQuestion mới
        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(question)
                .score(request.getScore())
                .build();

        exam.addExamQuestion(examQuestion); // đảm bảo liên kết 2 chiều

        ExamQuestion saved = examQuestionRepository.save(examQuestion);
        return mapExamQuestionToResponse(saved);
    }

    @Override
    @Transactional
    public void removeQuestionFromExam(Long examId, Long questionId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + examId));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + questionId));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question trong exam này"));

        examQuestionRepository.delete(examQuestion);
    }

    @Override
    public List<ExamQuestionResponse> getQuestionsInExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + examId));

        return examQuestionRepository.findByExam(exam)
                .stream()
                .map(this::mapExamQuestionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void publishExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + id));
        
        // Kiểm tra exam có câu hỏi không
        Integer questionCount = examQuestionRepository.countByExam(exam);
        if (questionCount == 0) {
            throw new BadRequestException("Không thể publish exam không có câu hỏi");
        }

        exam.setStatus("PUBLISHED");
        exam.setUpdatedAt(LocalDateTime.now());
        examRepository.save(exam);
    }

    @Override
    @Transactional
    public void archiveExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + id));
        
        exam.setStatus("ARCHIVED");
        exam.setUpdatedAt(LocalDateTime.now());
        examRepository.save(exam);
    }

    @Override
    public Page<ExamResponse> getPublishedExams(Pageable pageable) {
        return examRepository.findPublishedExams(pageable)
                .map(this::mapToResponse);
    }

    private ExamAttemptRow toRow(ExamAttempt a) {
        return ExamAttemptRow.builder()
                .attemptId(a.getId())
                .examId(a.getExam().getId())
                .examTitle(a.getExam().getTitle())
                .studentId(a.getUser().getUserId())
                .studentName(a.getUser().getFullName())
                .studentUsername(a.getUser().getUsername())
                .studentEmail(a.getUser().getEmail())
                .score(a.getScore())
                .gradedBy(a.getGradedBy())
                .startedAt(a.getStartedAt())
                .finishedAt(a.getFinishedAt())
                .status(null) // nếu có trường status trong Attempt thì set vào
                .build();
    }

    @Override
    public Page<ExamAttemptRow> listAttemptsOfExam(Long examId, int page, int size,
                                                   String keyword, LocalDateTime from, LocalDateTime to) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam với id " + examId));

        Page<ExamAttempt> p = examAttemptRepository.searchForTeacher(
                exam.getId(), (keyword == null || keyword.isBlank()) ? null : keyword, from, to,
                PageRequest.of(page, size, Sort.by("finishedAt").descending())
        );
        return p.map(this::toRow);
    }

    @Override
    public Page<ExamAttemptRow> listAttemptsOfStudent(Long studentId, int page, int size) {
        if (!userRepository.existsById(studentId)) {
            throw new NotFoundException("Không tìm thấy học sinh với id " + studentId);
        }
        Page<ExamAttempt> p = examAttemptRepository.findByUser_UserId(
                studentId, PageRequest.of(page, size, Sort.by("finishedAt").descending())
        );
        return p.map(this::toRow);
    }
}