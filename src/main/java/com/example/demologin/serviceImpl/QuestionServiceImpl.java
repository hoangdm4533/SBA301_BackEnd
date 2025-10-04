package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import com.example.demologin.entity.User;
import com.example.demologin.mapper.QuestionMapper;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.OptionRepository;
import com.example.demologin.repository.QuestionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepo;
    private final OptionRepository optionRepo;
    private final GradeRepository gradeRepo;
    private final UserRepository userRepo;      // để lấy teacher nếu cần
    private final QuestionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse get(Long id) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return mapper.toResponse(q);
    }

    @Override
    public QuestionResponse create(QuestionCreateRequest req) {
        // build base
        Question q = new Question();
        if (req.getTeacherId() != null) {
            User t = userRepo.findById(req.getTeacherId())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
            q.setTeacher(t);
        }
        q.setQuestionText(req.getQuestionText());
        q.setType(req.getType());
        q.setDifficulty(req.getDifficulty());
        q.setFormula(req.getFormula());
        q.setCreatedAt(LocalDateTime.now());
        q.setUpdatedAt(q.getCreatedAt());

        // lưu trước để có ID
        final Question saved = questionRepo.save(q);  // dùng biến mới, final

        // options
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            List<Option> options = req.getOptions().stream()
                    .map(o -> mapper.buildOption(saved, o))  // dùng 'saved' trong lambda
                    .toList();
            optionRepo.saveAll(options);
            saved.getOptions().addAll(options);
        }

        // grades (ManyToMany)
        if (req.getGradeIds() != null) {
            List<Grade> grades = gradeRepo.findAllById(req.getGradeIds());
            if (grades.size() != req.getGradeIds().size()) {
                throw new IllegalArgumentException("Some gradeIds are invalid");
            }
            saved.setGrades(grades);
        }

        // entity 'saved' đang được quản lý; gọi save lần nữa cũng được nhưng không bắt buộc
        questionRepo.save(saved);

        return mapper.toResponse(saved);
    }

    @Override
    public QuestionResponse update(Long id, QuestionUpdateRequest req) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (req.getQuestionText() != null) q.setQuestionText(req.getQuestionText());
        if (req.getType() != null) q.setType(req.getType());
        if (req.getDifficulty() != null) q.setDifficulty(req.getDifficulty());
        if (req.getFormula() != null) q.setFormula(req.getFormula());
        q.setUpdatedAt(LocalDateTime.now());

        // replace options nếu client gửi list
        if (req.getOptions() != null) {
            optionRepo.deleteByQuestion_Id(q.getId());
            q.getOptions().clear();

            if (!req.getOptions().isEmpty()) {
                List<Option> options = req.getOptions().stream()
                        .map(o -> mapper.buildOption(q, o))
                        .toList();
                optionRepo.saveAll(options);
                q.getOptions().addAll(options);
            }
        }

        // replace grades nếu client gửi list
        if (req.getGradeIds() != null) {
            List<Grade> grades = gradeRepo.findAllById(req.getGradeIds());
            if (grades.size() != req.getGradeIds().size()) {
                throw new IllegalArgumentException("Some gradeIds are invalid");
            }
            q.setGrades(grades);
        }

        return mapper.toResponse(q);
    }

    @Override
    public void delete(Long id) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // Xoá options trước (vì @OneToMany hiện chưa bật cascade/orphanRemoval)
        optionRepo.deleteByQuestion_Id(q.getId());

        // Xoá question (Hibernate sẽ xoá dòng join-table question_grades)
        questionRepo.delete(q);
    }
}
