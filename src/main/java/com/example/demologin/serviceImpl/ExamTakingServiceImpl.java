package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.exam_taking.FinishExamRequest;
import com.example.demologin.dto.request.exam_taking.StartExamRequest;
import com.example.demologin.dto.request.exam_taking.SubmitAnswerRequest;
import com.example.demologin.dto.response.*;
import com.example.demologin.entity.*;
import com.example.demologin.exception.exceptions.ConflictException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.exception.exceptions.BadRequestException;
import com.example.demologin.repository.*;
import com.example.demologin.service.ExamTakingService;
import com.example.demologin.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamTakingServiceImpl implements ExamTakingService {

    private final ExamTemplateRepository examTemplateRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final AccountUtils accountUtils;

    @Override
    public Page<AvailableExamResponse> getAvailableExams(Pageable pageable) {
        Page<ExamTemplate> examTemplates = examTemplateRepository.findByStatus("PUBLISHED", pageable);
        return mapToAvailableExamResponse(examTemplates);
    }

    @Override
    public Page<AvailableExamResponse> getAvailableExamsByDifficulty(String difficulty, Pageable pageable) {
        Page<ExamTemplate> examTemplates = examTemplateRepository.findAll(pageable);
        List<ExamTemplate> filteredByDifficulty = examTemplates.getContent().stream()
                .filter(exam -> "PUBLISHED".equals(exam.getStatus()) && difficulty.equals(exam.getDifficulty()))
                .collect(Collectors.toList());

        return new PageImpl<>(
                filteredByDifficulty.stream()
                        .map(this::mapToAvailableExamResponse)
                        .collect(Collectors.toList()),
                pageable,
                filteredByDifficulty.size()
        );
    }

    @Override
    public Page<AvailableExamResponse> getAvailableExamsByLevel(Long levelId, Pageable pageable) {
        Page<ExamTemplate> examTemplates = examTemplateRepository.findByLevelIdAndStatus(levelId, "PUBLISHED", pageable);
        return mapToAvailableExamResponse(examTemplates);
    }

    @Override
    @Transactional
    public ExamAttemptResponse startExam(StartExamRequest request) {
        User currentUser = accountUtils.getCurrentUser();

        ExamTemplate examTemplate = examTemplateRepository.findById(request.getExamTemplateId())
                .orElseThrow(() -> new NotFoundException("Exam template not found"));

        if (!"PUBLISHED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Exam is not available for taking");
        }

        // Check if user has any in-progress exam FOR A DIFFERENT EXAM TEMPLATE
        List<ExamAttempt> inProgressExams = examAttemptRepository.findInProgressExamsByStudent(currentUser.getUserId());
        log.debug("Found {} in-progress exams for user {}", inProgressExams.size(), currentUser.getUserId());

        // Filter to find in-progress exams for different exam templates (these would block starting a new exam)
        List<ExamAttempt> blockingExams = inProgressExams.stream()
                .filter(attempt -> !attempt.getExamTemplate().getId().equals(examTemplate.getId()))
                .collect(Collectors.toList());

        if (!blockingExams.isEmpty()) {
            ExamAttempt blockingExam = blockingExams.get(0);
            log.debug("Blocking exam: id={}, startedAt={}, finishedAt={}, examTemplate={}",
                     blockingExam.getId(), blockingExam.getStartedAt(), blockingExam.getFinishedAt(),
                     blockingExam.getExamTemplate() != null ? blockingExam.getExamTemplate().getTitle() : "null");
            throw new ConflictException("You have another exam in progress: " +
                blockingExam.getExamTemplate().getTitle() + ". Please finish it first before starting a new exam.");
        }

        // Check if there's an in-progress attempt for THIS specific exam template
        List<ExamAttempt> currentExamInProgress = inProgressExams.stream()
                .filter(attempt -> attempt.getExamTemplate().getId().equals(examTemplate.getId()))
                .collect(Collectors.toList());

        if (!currentExamInProgress.isEmpty()) {
            // User has an in-progress attempt for this exact exam - return the existing attempt instead of creating new one
            ExamAttempt existingAttempt = currentExamInProgress.get(0);
            log.debug("Found existing in-progress attempt for this exam: id={}, status: finished={}",
                     existingAttempt.getId(), existingAttempt.getFinishedAt() != null ? "YES" : "NO");

            // Double-check if this attempt is truly in progress
            if (existingAttempt.getFinishedAt() != null) {
                log.warn("Inconsistent state: attempt {} was returned as in-progress but has finishedAt={}",
                        existingAttempt.getId(), existingAttempt.getFinishedAt());
                // This attempt is actually finished, so create a new one
            } else {
                return mapToExamAttemptResponse(existingAttempt, examTemplate);
            }
        }

        // Create new exam attempt
        ExamAttempt examAttempt = ExamAttempt.builder()
                .examTemplate(examTemplate)
                .student(currentUser)
                .startedAt(LocalDateTime.now())
                .build();

        examAttempt = examAttemptRepository.save(examAttempt);

        return mapToExamAttemptResponse(examAttempt, examTemplate);
    }

    @Override
    public List<ExamQuestionForStudentResponse> getExamQuestions(Long examAttemptId) {
        User currentUser = accountUtils.getCurrentUser();

        ExamAttempt examAttempt = examAttemptRepository.findByIdAndStudent(examAttemptId, currentUser)
                .orElseThrow(() -> new NotFoundException("Exam attempt not found"));

        if (examAttempt.getFinishedAt() != null) {
            throw new BadRequestException("Exam has already been completed");
        }

        // Get questions from exam template
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamTemplateIdOrderByQuestionOrder(examAttempt.getExamTemplate().getId());

        return examQuestions.stream()
                .map(this::mapToExamQuestionForStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void submitAnswer(Long examAttemptId, SubmitAnswerRequest request) {
        log.debug("Starting submitAnswer - examAttemptId: {}, questionId: {}, optionId: {}",
                  examAttemptId, request.getQuestionId(), request.getOptionId());

        User currentUser = accountUtils.getCurrentUser();
        log.debug("Current user: {} (ID: {})", currentUser.getUsername(), currentUser.getUserId());

        // Debug: Check if exam attempt exists at all
        Optional<ExamAttempt> anyAttempt = examAttemptRepository.findById(examAttemptId);
        if (anyAttempt.isPresent()) {
            ExamAttempt attempt = anyAttempt.get();
            log.debug("Found exam attempt {} - belongs to student: {} (ID: {})",
                     examAttemptId, attempt.getStudent().getUsername(), attempt.getStudent().getUserId());
        } else {
            log.error("Exam attempt {} does not exist in database", examAttemptId);
        }

        ExamAttempt examAttempt = examAttemptRepository.findByIdAndStudent(examAttemptId, currentUser)
                .orElseThrow(() -> new NotFoundException("Exam attempt not found"));
        log.debug("Found exam attempt: {}", examAttempt.getId());

        if (examAttempt.getFinishedAt() != null) {
            log.warn("Attempt to submit answer for completed exam: attemptId={}, finishedAt={}, examTemplate={}",
                    examAttempt.getId(), examAttempt.getFinishedAt(), examAttempt.getExamTemplate().getTitle());
            throw new BadRequestException(String.format(
                "Exam attempt %d has already been completed at %s. Please start a new exam attempt to continue.",
                examAttempt.getId(), examAttempt.getFinishedAt()));
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Question not found"));
        log.debug("Found question: {} of type {}", question.getId(), question.getType());

        // Validate that the question belongs to this exam template
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamTemplateIdOrderByQuestionOrder(examAttempt.getExamTemplate().getId());
        log.debug("Found {} exam questions for template {}", examQuestions.size(), examAttempt.getExamTemplate().getId());
        boolean questionBelongsToExam = examQuestions.stream()
                .anyMatch(eq -> eq.getQuestion().getId().equals(question.getId()));

        if (!questionBelongsToExam) {
            log.error("Question {} does not belong to exam template {}. Available questions: {}",
                     question.getId(), examAttempt.getExamTemplate().getId(),
                     examQuestions.stream().map(eq -> eq.getQuestion().getId()).collect(Collectors.toList()));
            throw new BadRequestException("Question does not belong to this exam");
        }

        // Enforce answer type based on question type
        if ("MULTIPLE_CHOICE".equalsIgnoreCase(question.getType())) {
            if (request.getOptionId() == null) {
                throw new BadRequestException("optionId is required for multiple choice questions");
            }
            if (request.getEssayAnswer() != null && !request.getEssayAnswer().trim().isEmpty()) {
                throw new BadRequestException("essayAnswer must be null/empty for multiple choice questions");
            }
        } else if ("ESSAY".equalsIgnoreCase(question.getType())) {
            if (request.getEssayAnswer() == null || request.getEssayAnswer().trim().isEmpty()) {
                throw new BadRequestException("essayAnswer is required for essay questions");
            }
            if (request.getOptionId() != null) {
                throw new BadRequestException("optionId must be null for essay questions");
            }
        } else {
            // For any other types, keep the original mutual exclusivity rule
            if ((request.getOptionId() != null && request.getEssayAnswer() != null) ||
                (request.getOptionId() == null && (request.getEssayAnswer() == null || request.getEssayAnswer().trim().isEmpty()))) {
                throw new BadRequestException("Either optionId or essayAnswer must be provided, but not both");
            }
        }

        // Check if answer already exists and update it, or create new one
        StudentAnswer existingAnswer = studentAnswerRepository.findByAttemptAndQuestion(examAttempt, question)
                .orElse(null);

        if (existingAnswer != null) {
            log.debug("Updating existing answer: {}", existingAnswer.getId());
            // Update existing answer
            if (request.getOptionId() != null) {
                Option option = optionRepository.findById(request.getOptionId())
                        .orElseThrow(() -> new NotFoundException("Option not found"));
                log.debug("Found option: {} (linked question: {})", option.getId(),
                        option.getQuestion() != null ? option.getQuestion().getId() : null);

                // Validate that the option belongs to the question and is not orphaned
                if (option.getQuestion() == null || option.getQuestion().getId() == null) {
                    log.error("Option {} is not linked to any question", option.getId());
                    throw new BadRequestException("Option is not linked to any question");
                }
                if (!option.getQuestion().getId().equals(question.getId())) {
                    log.error("Option {} belongs to question {} but expected question {}",
                             option.getId(), option.getQuestion().getId(), question.getId());
                    throw new BadRequestException("Option does not belong to this question");
                }

                existingAnswer.setOption(option);
                existingAnswer.setEssayAnswer(null);
            } else {
                existingAnswer.setEssayAnswer(request.getEssayAnswer());
                existingAnswer.setOption(null);
            }
            studentAnswerRepository.save(existingAnswer);
            log.debug("Updated existing answer successfully");
        } else {
            log.debug("Creating new answer");
            // Create new answer
            StudentAnswer.StudentAnswerBuilder answerBuilder = StudentAnswer.builder()
                    .attempt(examAttempt)
                    .question(question);

            if (request.getOptionId() != null) {
                Option option = optionRepository.findById(request.getOptionId())
                        .orElseThrow(() -> new NotFoundException("Option not found"));
                log.debug("Found option for new answer: {} (linked question: {})", option.getId(),
                        option.getQuestion() != null ? option.getQuestion().getId() : null);

                // Validate that the option belongs to the question and is not orphaned
                if (option.getQuestion() == null || option.getQuestion().getId() == null) {
                    log.error("Option {} is not linked to any question", option.getId());
                    throw new BadRequestException("Option is not linked to any question");
                }
                if (!option.getQuestion().getId().equals(question.getId())) {
                    log.error("Option {} belongs to question {} but expected question {}",
                             option.getId(), option.getQuestion().getId(), question.getId());
                    throw new BadRequestException("Option does not belong to this question");
                }

                answerBuilder.option(option);
            } else {
                answerBuilder.essayAnswer(request.getEssayAnswer());
            }

            StudentAnswer newAnswer = answerBuilder.build();
            studentAnswerRepository.save(newAnswer);
            log.debug("Created new answer successfully");
        }

        log.debug("submitAnswer completed successfully");
    }

    @Override
    public ExamAttemptResponse getCurrentExamAttempt(Long examAttemptId) {
        User currentUser = accountUtils.getCurrentUser();

        ExamAttempt examAttempt = examAttemptRepository.findByIdAndStudent(examAttemptId, currentUser)
                .orElseThrow(() -> new NotFoundException("Exam attempt not found"));

        return mapToExamAttemptResponse(examAttempt, examAttempt.getExamTemplate());
    }

    @Override
    @Transactional
    public ExamResultResponse finishExam(FinishExamRequest request) {
        User currentUser = accountUtils.getCurrentUser();

        ExamAttempt examAttempt = examAttemptRepository.findByIdAndStudent(request.getExamAttemptId(), currentUser)
                .orElseThrow(() -> new NotFoundException("Exam attempt not found"));

        if (examAttempt.getFinishedAt() != null) {
            throw new BadRequestException("Exam has already been completed");
        }

        // Check if exam has timed out
        if (examAttempt.getExamTemplate().getDuration() != null) {
            long minutesElapsed = Duration.between(examAttempt.getStartedAt(), LocalDateTime.now()).toMinutes();
            if (minutesElapsed > examAttempt.getExamTemplate().getDuration()) {
                log.warn("Exam attempt {} has exceeded time limit. Auto-finishing.", examAttempt.getId());
            }
        }

        // Submit any remaining answers
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            for (SubmitAnswerRequest answer : request.getAnswers()) {
                try {
                    submitAnswer(request.getExamAttemptId(), answer);
                } catch (Exception e) {
                    log.warn("Failed to submit answer for question {}: {}", answer.getQuestionId(), e.getMessage());
                    // Continue processing other answers instead of failing completely
                }
            }
        }

        // Mark exam as finished
        examAttempt.setFinishedAt(LocalDateTime.now());

        // Calculate score
        Double score = calculateScore(examAttempt);
        examAttempt.setScore(score);

        examAttemptRepository.save(examAttempt);

        return buildExamResultResponse(examAttempt);
    }

    @Override
    public ExamResultResponse getExamResult(Long examAttemptId) {
        User currentUser = accountUtils.getCurrentUser();

        ExamAttempt examAttempt = examAttemptRepository.findByIdAndStudent(examAttemptId, currentUser)
                .orElseThrow(() -> new NotFoundException("Exam attempt not found"));

        if (examAttempt.getFinishedAt() == null) {
            throw new BadRequestException("Exam has not been completed yet");
        }

        return buildExamResultResponse(examAttempt);
    }

    @Override
    public Page<ExamAttemptResponse> getMyExamHistory(Pageable pageable) {
        User currentUser = accountUtils.getCurrentUser();

        Page<ExamAttempt> examAttempts = examAttemptRepository.findByStudentOrderByStartedAtDesc(currentUser, pageable);

        return examAttempts.map(attempt -> mapToExamAttemptResponse(attempt, attempt.getExamTemplate()));
    }

    @Override
    public List<ExamAttemptResponse> getInProgressExams() {
        User currentUser = accountUtils.getCurrentUser();

        List<ExamAttempt> inProgressExams = examAttemptRepository.findInProgressExamsByStudent(currentUser.getUserId());

        return inProgressExams.stream()
                .map(attempt -> mapToExamAttemptResponse(attempt, attempt.getExamTemplate()))
                .collect(Collectors.toList());
    }

    @Override
    public Object debugCheckData() {
        try {
            User currentUser = accountUtils.getCurrentUser();
            log.debug("Debug check data - Current user: {}", currentUser.getUserId());

            // Check exam attempts
            List<ExamAttempt> allAttempts = examAttemptRepository.findByStudentOrderByStartedAtDesc(currentUser);
            log.debug("Found {} exam attempts for user", allAttempts.size());

            // Check questions
            List<Question> questions = questionRepository.findAll();
            log.debug("Found {} questions in database", questions.size());

            // Check options
            List<Option> options = optionRepository.findAll();
            log.debug("Found {} options in database", options.size());

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("currentUser", currentUser.getUserId());
            debugInfo.put("examAttemptsCount", allAttempts.size());
            debugInfo.put("questionsCount", questions.size());
            debugInfo.put("optionsCount", options.size());

            if (!allAttempts.isEmpty()) {
                debugInfo.put("latestAttempt", allAttempts.get(0).getId());
            }

            if (!questions.isEmpty()) {
                debugInfo.put("firstQuestionId", questions.get(0).getId());
            }

            if (!options.isEmpty()) {
                debugInfo.put("firstOptionId", options.get(0).getId());
            }

            return debugInfo;
        } catch (Exception e) {
            log.error("Debug check data failed", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            return errorInfo;
        }
    }

    @Override
    @Transactional
    public Object cleanupInconsistentState() {
        try {
            User currentUser = accountUtils.getCurrentUser();
            log.debug("Cleanup inconsistent state for user: {}", currentUser.getUserId());

            // Find all in-progress exams for the user
            List<ExamAttempt> inProgressExams = examAttemptRepository.findInProgressExamsByStudent(currentUser.getUserId());

            Map<String, Object> result = new HashMap<>();
            result.put("currentUser", currentUser.getUserId());
            result.put("foundInProgressExams", inProgressExams.size());

            int cleanedUp = 0;
            for (ExamAttempt examAttempt : inProgressExams) {
                log.debug("Checking exam attempt: id={}, startedAt={}, finishedAt={}",
                         examAttempt.getId(), examAttempt.getStartedAt(), examAttempt.getFinishedAt());

                // If finishedAt is null but there are inconsistencies, mark as finished with current time
                if (examAttempt.getFinishedAt() == null) {
                    // Check if this exam has been running too long (more than 4 hours) OR force cleanup for inconsistent states
                    long minutesElapsed = Duration.between(examAttempt.getStartedAt(), LocalDateTime.now()).toMinutes();

                    // Force cleanup for any in-progress exam that's causing conflicts
                    // This resolves the "can't start new exam" vs "exam already completed" conflict
                    log.warn("Force-finishing inconsistent exam attempt {} that has been running for {} minutes",
                            examAttempt.getId(), minutesElapsed);

                    examAttempt.setFinishedAt(LocalDateTime.now());

                    // Calculate score based on existing answers
                    List<StudentAnswer> answers = studentAnswerRepository.findByAttemptOrderByQuestionId(examAttempt);
                    long correctAnswers = answers.stream()
                            .mapToLong(answer -> (answer.getOption() != null && answer.getOption().getIsCorrect() != null && answer.getOption().getIsCorrect()) ? 1 : 0)
                            .sum();
                    examAttempt.setScore((double) correctAnswers);

                    examAttemptRepository.save(examAttempt);
                    cleanedUp++;
                }
            }

            result.put("cleanedUpExams", cleanedUp);

            // Check again after cleanup
            List<ExamAttempt> remainingInProgress = examAttemptRepository.findInProgressExamsByStudent(currentUser.getUserId());
            result.put("remainingInProgressExams", remainingInProgress.size());

            return result;

        } catch (Exception e) {
            log.error("Cleanup failed", e);
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            return errorInfo;
        }
    }

    // Helper methods
    private Page<AvailableExamResponse> mapToAvailableExamResponse(Page<ExamTemplate> examTemplates) {
        return examTemplates.map(this::mapToAvailableExamResponse);
    }

    private AvailableExamResponse mapToAvailableExamResponse(ExamTemplate examTemplate) {
        return AvailableExamResponse.builder()
                .id(examTemplate.getId())
                .title(examTemplate.getTitle())
                .description(examTemplate.getDescription())
                .difficulty(examTemplate.getDifficulty())
                .duration(examTemplate.getDuration())
                .totalQuestions(examTemplate.getTotalQuestions())
                .totalPoints(examTemplate.getTotalPoints())
                .levelName(examTemplate.getLevel() != null ? examTemplate.getLevel().getName() : null)
                .levelId(examTemplate.getLevel() != null ? examTemplate.getLevel().getId() : null)
                .build();
    }

    private ExamAttemptResponse mapToExamAttemptResponse(ExamAttempt examAttempt, ExamTemplate examTemplate) {
        String status = examAttempt.getFinishedAt() != null ? "COMPLETED" : "IN_PROGRESS";

        Integer remainingTime = null;
        if (examTemplate != null && examAttempt.getFinishedAt() == null && examTemplate.getDuration() != null) {
            long minutesElapsed = Duration.between(examAttempt.getStartedAt(), LocalDateTime.now()).toMinutes();
            remainingTime = Math.max(0, examTemplate.getDuration() - (int) minutesElapsed);
            if (remainingTime == 0) {
                status = "TIMEOUT";
            }
        }

        return ExamAttemptResponse.builder()
                .id(examAttempt.getId())
                .examTemplateId(examTemplate != null ? examTemplate.getId() : null)
                .examTitle(examTemplate != null ? examTemplate.getTitle() : "Unknown Exam")
                .startedAt(examAttempt.getStartedAt())
                .finishedAt(examAttempt.getFinishedAt())
                .score(examAttempt.getScore())
                .status(status)
                .duration(examTemplate != null ? examTemplate.getDuration() : null)
                .remainingTime(remainingTime)
                .build();
    }

    private ExamQuestionForStudentResponse mapToExamQuestionForStudentResponse(ExamQuestion examQuestion) {
        Question question = examQuestion.getQuestion();

        List<OptionForStudentResponse> options = null;
        if ("MULTIPLE_CHOICE".equals(question.getType())) {
            options = question.getOptions().stream()
                    .map(option -> OptionForStudentResponse.builder()
                            .id(option.getId())
                            .optionText(option.getOptionText())
                            .build())
                    .collect(Collectors.toList());
        }

        return ExamQuestionForStudentResponse.builder()
                .id(examQuestion.getId())
                .questionId(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getType())
                .questionOrder(examQuestion.getQuestionOrder())
                .points(examQuestion.getPoints())
                .options(options)
                .build();
    }

    private Double calculateScore(ExamAttempt examAttempt) {
        List<StudentAnswer> answers = studentAnswerRepository.findByAttempt(examAttempt);
        double totalScore = 0.0;

        for (StudentAnswer answer : answers) {
            if (answer.getOption() != null && answer.getOption().getIsCorrect() != null && answer.getOption().getIsCorrect()) {
                // Use 1.0 point for each correct answer (simplified scoring)
                totalScore += 1.0;
            }
            // Essay questions would need manual grading
        }

        return totalScore;
    }

    private ExamResultResponse buildExamResultResponse(ExamAttempt examAttempt) {
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptOrderByQuestionId(examAttempt);

        List<QuestionResultResponse> questionResults = answers.stream()
                .map(this::mapToQuestionResultResponse)
                .collect(Collectors.toList());

        long correctAnswers = answers.stream()
                .mapToLong(answer -> (answer.getOption() != null && answer.getOption().getIsCorrect() != null && answer.getOption().getIsCorrect()) ? 1 : 0)
                .sum();

        // Simplified max score calculation
        double maxScore = answers.size(); // 1 point per question

        double percentage = maxScore > 0 ? (examAttempt.getScore() / maxScore) * 100 : 0;

        Long durationMinutes = examAttempt.getFinishedAt() != null ?
                Duration.between(examAttempt.getStartedAt(), examAttempt.getFinishedAt()).toMinutes() : null;

        return ExamResultResponse.builder()
                .attemptId(examAttempt.getId())
                .examTitle(examAttempt.getExamTemplate() != null ? examAttempt.getExamTemplate().getTitle() : "Unknown Exam")
                .score(examAttempt.getScore())
                .maxScore(maxScore)
                .percentage(percentage)
                .startedAt(examAttempt.getStartedAt())
                .finishedAt(examAttempt.getFinishedAt())
                .durationMinutes(durationMinutes)
                .totalQuestions(answers.size())
                .correctAnswers((int) correctAnswers)
                .questionResults(questionResults)
                .build();
    }

    private QuestionResultResponse mapToQuestionResultResponse(StudentAnswer answer) {
        String studentAnswer = answer.getOption() != null ?
                answer.getOption().getOptionText() : answer.getEssayAnswer();

        String correctAnswer = answer.getQuestion().getOptions().stream()
                .filter(option -> option.getIsCorrect() != null && option.getIsCorrect())
                .map(Option::getOptionText)
                .findFirst()
                .orElse("Essay question - manual grading required");

        boolean isCorrect = answer.getOption() != null && answer.getOption().getIsCorrect() != null && answer.getOption().getIsCorrect();

        double maxPoints = 1.0; // Simplified scoring
        double pointsEarned = isCorrect ? maxPoints : 0.0;

        return QuestionResultResponse.builder()
                .questionId(answer.getQuestion().getId())
                .questionText(answer.getQuestion().getQuestionText())
                .studentAnswer(studentAnswer)
                .correctAnswer(correctAnswer)
                .pointsEarned(pointsEarned)
                .maxPoints(maxPoints)
                .isCorrect(isCorrect)
                .build();
    }
}

