package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.*;
import com.example.demologin.mapper.QuestionMapper;
import com.example.demologin.repository.*;
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
    private final QuestionTypeRepository questionTypeRepo;
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
        Question q = new Question();
        q.setQuestionText(req.getQuestionText());
        q.setFormula(req.getFormula());
        q.setCreatedAt(LocalDateTime.now());
        q.setUpdatedAt(q.getCreatedAt());

        // map type (String -> QuestionType) theo description (case-insensitive)
        if (req.getType() != null && !req.getType().isBlank()) {
            QuestionType qt = questionTypeRepo.findByDescriptionIgnoreCase(req.getType().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question type: " + req.getType()));
            q.setType(qt);
        } else {
            q.setType(null);
        }

        final Question saved = questionRepo.save(q);

        // options
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            List<Option> options = req.getOptions().stream()
                    .map(o -> mapper.buildOption(saved, o))
                    .toList();
            optionRepo.saveAll(options);
            saved.setOptions(options);
        }

        return mapper.toResponse(saved);
    }

    @Override
    public QuestionResponse update(Long id, QuestionUpdateRequest req) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (req.getQuestionText() != null) q.setQuestionText(req.getQuestionText());
        if (req.getFormula() != null) q.setFormula(req.getFormula());

        if (req.getType() != null) {
            if (req.getType().isBlank()) {
                q.setType(null);
            } else {
                QuestionType qt = questionTypeRepo.findByDescriptionIgnoreCase(req.getType().trim())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid question type: " + req.getType()));
                q.setType(qt);
            }
        }

        q.setUpdatedAt(LocalDateTime.now());

        // replace options nếu client gửi list
        if (req.getOptions() != null) {
            optionRepo.deleteByQuestion_Id(q.getId());
            q.setOptions(List.of());
            if (!req.getOptions().isEmpty()) {
                List<Option> options = req.getOptions().stream()
                        .map(o -> mapper.buildOption(q, o))
                        .toList();
                optionRepo.saveAll(options);
                q.setOptions(options);
            }
        }

        return mapper.toResponse(q);
    }

    @Override
    public void delete(Long id) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        optionRepo.deleteByQuestion_Id(q.getId());
        questionRepo.delete(q);
    }
}
