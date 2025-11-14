    package com.example.demologin.service.impl;

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
    private final StudentAnswerRepository studentAnswerRepository;
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
                card.setDurationMinutes(e.getDurationMinutes());
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

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = null;

            // Nếu đề thi có thời gian làm bài, tính thời điểm hết hạn
            if (exam.getDurationMinutes() != null && exam.getDurationMinutes() > 0) {
                expiresAt = now.plusMinutes(exam.getDurationMinutes());
            }

            ExamAttempt attempt = new ExamAttempt();
            attempt.setExam(exam);
            attempt.setUser(currentUser);
            attempt.setStartedAt(now);
            attempt.setExpiresAt(expiresAt);
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
                            ov.setContent(o.getOptionText()); //
                            return ov;
                        })
                        .toList();

                QuestionView qv = new QuestionView();
                qv.setId(q.getId());
                qv.setText(q.getQuestionText());
                qv.setQuestionType(q.getType() != null ? q.getType().getDescription() : null); // 🔁 nếu DTO bạn dùng 'questionType' thì đổi tên setter
                qv.setOptions(optionViews);
                qv.setScore(eq.getScore()); // gửi điểm từng câu để FE hiển thị nếu cần
                return qv;
            }).toList();

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

            // Kiểm tra nếu đã nộp rồi
            if (attempt.getFinishedAt() != null) {
                throw new ForbiddenException("This attempt has already been submitted");
            }

            // Kiểm tra nếu quá hạn (có thể log nếu cần)
            LocalDateTime now = LocalDateTime.now();
            if (attempt.getExpiresAt() != null && now.isAfter(attempt.getExpiresAt())) {
                // Đã hết thời gian - vẫn cho nộp nhưng có thể xử lý thêm (log, notification, etc.)
                // TODO: log timeout if needed
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

                    Question question = eq.getQuestion();

                    // Đáp án đúng
                    List<Long> correctIds = optionRepository.findByQuestion_IdAndIsCorrectTrue(qid)
                            .stream().map(Option::getId).toList();

                    // Lựa chọn của user
                    List<Long> chosen = (ans.getSelectedOptionIds() == null)
                            ? Collections.emptyList()
                            : ans.getSelectedOptionIds();

                    // So sánh theo tập hợp (Set)
                    boolean isCorrect = new HashSet<>(chosen).equals(new HashSet<>(correctIds));
                    if (isCorrect) {
                        totalCorrect++;
                        double qScore = eq.getScore() == null ? 1.0 : eq.getScore().doubleValue();
                        totalScore += qScore;
                    }

                    // Lưu mỗi lựa chọn của học sinh vào StudentAnswer
                    for (Long optionId : chosen) {
                        Option selectedOption = optionRepository.findById(optionId).orElse(null);
                        if (selectedOption != null) {
                            StudentAnswer studentAnswer = new StudentAnswer();
                            studentAnswer.setExamAttempt(attempt);
                            studentAnswer.setQuestion(question);
                            studentAnswer.setOption(selectedOption);
                            studentAnswer.setUser(currentUser);
                            // Nếu là MCQ_SINGLE, lưu score của toàn câu
                            // Nếu là MCQ_MULTI hoặc TRUE_FALSE, có thể chia score cho từng option
                            studentAnswer.setScore(isCorrect ? (eq.getScore() != null ? eq.getScore() : 1.0) : 0.0);
                            studentAnswerRepository.save(studentAnswer);
                        }
                    }
                }
            }

            attempt.setFinishedAt(LocalDateTime.now());
            attempt.setScore(totalScore);
            examAttemptRepository.save(attempt);

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

        @Override
        @Transactional
        public AttemptDetailResponse getAttemptDetail(Long attemptId) {
            ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                    .orElseThrow(() -> new EntityNotFoundException("Attempt not found"));

            User currentUser = accountUtils.getCurrentUser();
            if (!attempt.getUser().getUserId().equals(currentUser.getUserId())) {
                throw new ForbiddenException("You cannot view someone else's attempt");
            }

            Exam exam = attempt.getExam();
            List<ExamQuestion> eqs = examQuestionRepository.findByExam(exam);
            List<StudentAnswer> studentAnswers = studentAnswerRepository.findByAttemptId(attemptId);

            // Map student answers by question ID
            Map<Long, List<StudentAnswer>> answersByQuestionId = studentAnswers.stream()
                    .collect(Collectors.groupingBy(sa -> sa.getQuestion().getId()));

            double maxScore = eqs.stream()
                    .map(ExamQuestion::getScore)
                    .map(s -> s == null ? 1.0 : s.doubleValue())
                    .reduce(0.0, Double::sum);

            int totalCorrect = 0;

            List<AttemptDetailResponse.AttemptQuestionDetail> questionDetails = new ArrayList<>();

            for (ExamQuestion eq : eqs) {
                Question question = eq.getQuestion();
                List<StudentAnswer> answers = answersByQuestionId.getOrDefault(question.getId(), Collections.emptyList());

                // Đáp án đúng
                List<Long> correctOptionIds = optionRepository.findByQuestion_IdAndIsCorrectTrue(question.getId())
                        .stream().map(Option::getId).toList();

                // Lựa chọn của user
                List<Long> selectedOptionIds = answers.stream()
                        .map(sa -> sa.getOption().getId())
                        .toList();

                // Kiểm tra đúng/sai
                boolean isCorrect = new HashSet<>(selectedOptionIds).equals(new HashSet<>(correctOptionIds));
                if (isCorrect) {
                    totalCorrect++;
                }

                // Build question detail
                AttemptDetailResponse.AttemptQuestionDetail qDetail = new AttemptDetailResponse.AttemptQuestionDetail();
                qDetail.setQuestionId(question.getId());
                qDetail.setQuestionText(question.getQuestionText());
                qDetail.setQuestionType(question.getType() != null ? question.getType().getDescription() : null);
                qDetail.setScore(isCorrect ? (eq.getScore() != null ? eq.getScore() : 1.0) : 0.0);
                qDetail.setMaxScore(eq.getScore() != null ? eq.getScore() : 1.0);
                qDetail.setCorrect(isCorrect);
                qDetail.setSelectedOptionIds(selectedOptionIds);
                qDetail.setCorrectOptionIds(correctOptionIds);

                // Build options list
                List<AttemptDetailResponse.OptionDetail> optionDetails = new ArrayList<>();
                if (question.getOptions() != null) {
                    for (Option option : question.getOptions()) {
                        AttemptDetailResponse.OptionDetail oDetail = new AttemptDetailResponse.OptionDetail();
                        oDetail.setId(option.getId());
                        oDetail.setText(option.getOptionText());
                        oDetail.setIsCorrect(option.getIsCorrect());
                        oDetail.setIsSelected(selectedOptionIds.contains(option.getId()));
                        optionDetails.add(oDetail);
                    }
                }
                qDetail.setOptions(optionDetails);

                questionDetails.add(qDetail);
            }

            // Build response
            AttemptDetailResponse response = new AttemptDetailResponse();
            response.setAttemptId(attempt.getId());
            response.setExamId(exam.getId());
            response.setExamTitle(exam.getTitle());
            response.setStartedAt(attempt.getStartedAt());
            response.setFinishedAt(attempt.getFinishedAt());
            response.setScore(attempt.getScore() != null ? attempt.getScore() : 0.0);
            response.setMaxScore(maxScore);
            response.setTotalQuestions(eqs.size());
            response.setCorrectAnswers(totalCorrect);
            response.setQuestions(questionDetails);

            return response;
        }

    }


