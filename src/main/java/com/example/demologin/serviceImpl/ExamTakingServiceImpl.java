    package com.example.demologin.serviceImpl;

    import com.example.demologin.dto.request.exam.AnswerPayload;
    import com.example.demologin.dto.request.exam.ExamSubmitRequest;

    import com.example.demologin.dto.response.*;
    import com.example.demologin.entity.*;
    import com.example.demologin.exception.exceptions.ForbiddenException;
    import com.example.demologin.exception.exceptions.NotFoundException;
    import com.example.demologin.repository.*;
    import com.example.demologin.service.ExamTakingService;
    import com.example.demologin.utils.AccountUtils;
    import jakarta.persistence.EntityNotFoundException;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageImpl;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;

    import java.time.Instant;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.*;
    import java.util.function.Function;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class ExamTakingServiceImpl implements ExamTakingService {
        private final ExamAttemptRepository examAttemptRepository;
        private final ExamQuestionRepository examQuestionRepository;
        private final OptionRepository optionRepository;
        private final AccountUtils accountUtils;
        private final ExamRepository examRepository;


        @Override
        public Page<ExamCard> listAvailable(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);

            // lấy danh sách bài thi có status = "PUBLISHED"
            Page<Exam> exams = examRepository.findByStatusIgnoreCase("PUBLISHED", pageable);

            return exams.map(e -> {
                ExamCard card = new ExamCard();
                card.setId(e.getId());
                card.setTitle(e.getTitle());
                card.setDescription(e.getDescription());
                card.setStatus(e.getStatus());
                card.setQuestionCount(
                        e.getExamQuestions() != null ? e.getExamQuestions().size() : 0
                );
                return card;
            });
        }

        @Transactional
        @Override
        public ExamStartResponse startAttempt(Long examId) {
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Exam not found: " + examId));

            if (exam.getStatus() == null || !exam.getStatus().equalsIgnoreCase("PUBLISHED")) {
                throw new com.example.demologin.exception.exceptions.ForbiddenException("Exam is not published");
            }

            User currentUser = accountUtils.getCurrentUser();

            ExamAttempt attempt = new ExamAttempt();
            attempt.setExam(exam);
            attempt.setUser(currentUser);
            attempt.setStartedAt(LocalDateTime.now());
            attempt = examAttemptRepository.save(attempt);

            List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);

            List<QuestionView> questions = eqs.stream().map(eq -> {
                Question q = eq.getQuestion();

                // Map options (KHÔNG đụng tới qv ở đây)
                List<OptionView> optionViews = (q.getOptions() == null ? List.<Option>of() : q.getOptions())
                        .stream()
                        .map(o -> {
                            OptionView ov = new OptionView();
                            ov.setId(o.getId());
                            ov.setContent(o.getOptionText()); // đổi nếu field khác
                            return ov;
                        })
                        .toList();

                // Tạo qv sau khi đã có optionViews
                QuestionView qv = new QuestionView();
                qv.setId(q.getId());
                qv.setText(q.getQuestionText()); // đổi nếu field khác
                qv.setQuestionType(q.getType() != null ? q.getType().getDescription() : null);
                qv.setOptions(optionViews);
                qv.setScore(eq.getScore()); // set score ở đây
                return qv;
            }).toList();

            ExamStartResponse resp = new ExamStartResponse();
            resp.setAttemptId(attempt.getId());
            resp.setExamId(exam.getId());
            resp.setTitle(exam.getTitle());
            resp.setTotalQuestions(questions.size());
            resp.setStartedAt(attempt.getStartedAt().atZone(ZoneId.systemDefault()).toInstant());
            resp.setMustSubmitBefore(null);
            resp.setQuestions(questions);
            return resp;
        }

        @Transactional
        @Override
        public ExamSubmitResponse submitAttempt(Long attemptId, ExamSubmitRequest req) {
            ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                    .orElseThrow(() -> new EntityNotFoundException("Attempt not found"));

            User currentUser = accountUtils.getCurrentUser();
            if (!attempt.getUser().getUserId().equals(currentUser.getUserId())) {
                throw new ForbiddenException("You cannot submit someone else's attempt");
            }

            Exam exam = attempt.getExam();

            List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);
            Map<Long, ExamQuestion> examQuestionsByQid = eqs.stream()
                    .collect(Collectors.toMap(eq -> eq.getQuestion().getId(), Function.identity()));

            int totalQuestions = (req.getAnswers() == null) ? 0 : req.getAnswers().size();
            int totalCorrect = 0;

            if (req.getAnswers() != null) {
                for (AnswerPayload ans : req.getAnswers()) {
                    Long qid = ans.getQuestionId();
                    if (!examQuestionsByQid.containsKey(qid)) continue;

                    List<Long> correctIds = optionRepository.findByQuestion_IdAndIsCorrectTrue(qid)
                            .stream().map(Option::getId).toList();

                    List<Long> chosen = ans.getSelectedOptionIds() == null
                            ? Collections.emptyList()
                            : ans.getSelectedOptionIds();

                    boolean isCorrect = new HashSet<>(chosen).equals(new HashSet<>(correctIds));
                    if (isCorrect) totalCorrect++;

                }
            }

            double score = (totalQuestions == 0) ? 0.0 : (totalCorrect * 1.0); // hoặc scale theo điểm từng câu

            attempt.setFinishedAt(LocalDateTime.now());
            attempt.setScore(score);
            examAttemptRepository.save(attempt);

            ExamSubmitResponse resp = new ExamSubmitResponse();
            resp.setAttemptId(attempt.getId());
            resp.setScore(score);
            resp.setTotalCorrect(totalCorrect);
            resp.setTotalQuestions(totalQuestions);
            return resp;
        }

        @Override
        public Page<AttemptSummary> myAttempts(int page, int size) {
            User currentUser = accountUtils.getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);

            Page<ExamAttempt> attempts = examAttemptRepository.findByUser(currentUser, pageable);

            return attempts.map(a -> {
                AttemptSummary dto = new AttemptSummary();
                dto.setAttemptId(a.getId());
                dto.setExamId(a.getExam() != null ? a.getExam().getId() : null);
                dto.setExamTitle(a.getExam() != null ? a.getExam().getTitle() : null);
                dto.setScore(a.getScore());
                dto.setSubmittedAt(a.getFinishedAt() != null ? a.getFinishedAt().atZone(ZoneId.systemDefault()).toInstant() : null);
                return dto;
            });
        }

    }

