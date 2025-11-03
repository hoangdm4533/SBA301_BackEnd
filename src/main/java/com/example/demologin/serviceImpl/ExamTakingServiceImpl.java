    package com.example.demologin.serviceImpl;

    import com.example.demologin.dto.request.exam.AnswerPayload;
    import com.example.demologin.dto.request.exam.ExamSubmitRequest;

    import com.example.demologin.dto.response.*;
    import com.example.demologin.entity.*;
    import com.example.demologin.exception.exceptions.ForbiddenException;
    import com.example.demologin.mapper.examattempt.ExamAttemptMapper;
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

    import java.time.LocalDateTime;
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
        private final ExamAttemptMapper examAttemptMapper;


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
                    .orElseThrow(() -> new EntityNotFoundException("Exam not found: " + examId));

            if (exam.getStatus() == null || !exam.getStatus().equalsIgnoreCase("PUBLISHED")) {
                throw new ForbiddenException("Exam is not published");
            }

            User currentUser = accountUtils.getCurrentUser();

            ExamAttempt attempt = new ExamAttempt();
            attempt.setExam(exam);
            attempt.setUser(currentUser);
            attempt.setStartedAt(LocalDateTime.now());
            attempt = examAttemptRepository.save(attempt);

            // Lấy câu hỏi của đề + map về QuestionView (không lộ đáp án)
            List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);

            List<QuestionView> questionViews = eqs.stream().map(eq -> {
                Question q = eq.getQuestion();

                List<OptionView> optionViews = (q.getOptions() == null ? List.<Option>of() : q.getOptions())
                        .stream()
                        .map(o -> {
                            OptionView ov = new OptionView();
                            ov.setId(o.getId());
                            ov.setContent(o.getOptionText()); // 🔁 nếu DTO bạn dùng 'content' thì đổi setContent(...)
                            return ov;
                        })
                        .toList();

                QuestionView qv = new QuestionView();
                qv.setId(q.getId());
                qv.setText(q.getQuestionText()); // 🔁 nếu DTO bạn dùng 'text' thì đổi setText(...)
                qv.setQuestionType(q.getType() != null ? q.getType().getDescription() : null); // 🔁 nếu DTO bạn dùng 'questionType' thì đổi tên setter
                qv.setOptions(optionViews);
                qv.setScore(eq.getScore()); // gửi điểm từng câu để FE hiển thị nếu cần
                return qv;
            }).toList();

            // ✅ Trả về qua mapper (đúng kiểu List<QuestionView>)
            return examAttemptMapper.toStartResponse(
                    attempt,
                    questionViews.size(),
                    questionViews
            );
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

            // Câu hỏi + điểm từng câu
            List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);
            Map<Long, ExamQuestion> examQuestionsByQid = eqs.stream()
                    .collect(Collectors.toMap(eq -> eq.getQuestion().getId(), Function.identity()));

            int totalQuestions = eqs.size();
            int totalCorrect   = 0;
            double totalScore  = 0.0;
            double maxScore    = eqs.stream()
                    .map(ExamQuestion::getScore)
                    .map(s -> s == null ? 1.0 : s.doubleValue())
                    .reduce(0.0, Double::sum);

            if (req.getAnswers() != null) {
                for (AnswerPayload ans : req.getAnswers()) {
                    Long qid = ans.getQuestionId();
                    ExamQuestion eq = examQuestionsByQid.get(qid);
                    if (eq == null) continue; // câu không thuộc đề

                    // Đáp án đúng
                    List<Long> correctIds = optionRepository.findByQuestion_IdAndIsCorrectTrue(qid)
                            .stream().map(Option::getId).toList();

                    // Lựa chọn của user
                    List<Long> chosen = (ans.getSelectedOptionIds() == null)
                            ? Collections.emptyList()
                            : ans.getSelectedOptionIds();

                    // So sánh theo tập hợp
                    boolean isCorrect = new HashSet<>(chosen).equals(new HashSet<>(correctIds));
                    if (isCorrect) {
                        totalCorrect++;
                        double qScore = eq.getScore() == null ? 1.0 : eq.getScore().doubleValue();
                        totalScore += qScore;
                    }

                    // TODO: nếu có SHORT_ANSWER thì xử lý ans.getAnswerText() tại đây
                }
            }

            attempt.setFinishedAt(LocalDateTime.now());
            attempt.setScore(totalScore);
            examAttemptRepository.save(attempt);

            // ✅ Trả về qua mapper
            return examAttemptMapper.toSubmitResponse(
                    attempt,
                    maxScore,
                    totalQuestions,
                    totalCorrect
            );
        }

        @Override
        @Transactional
        public Page<AttemptSummary> myAttempts(int page, int size) {
            var currentUser = accountUtils.getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);

            Page<ExamAttempt> attempts = examAttemptRepository.findByUser_UserId(currentUser.getUserId(), pageable);

            List<AttemptSummary> summaries = attempts.getContent().stream().map(attempt -> {
                Exam exam = attempt.getExam();
                List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);

                double maxScore = eqs.stream()
                        .map(ExamQuestion::getScore)
                        .map(s -> s == null ? 1.0 : s.doubleValue())
                        .reduce(0.0, Double::sum);

                return examAttemptMapper.toMyAttemptResponse(attempt, maxScore, eqs.size());
            }).toList();

            return new PageImpl<>(summaries, pageable, attempts.getTotalElements());
        }

    }

