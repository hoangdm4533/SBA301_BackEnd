package com.example.demologin.service.impl;

import com.example.demologin.dto.request.questiontype.QuestionTypeCreateRequest;
import com.example.demologin.dto.request.questiontype.QuestionTypeUpdateRequest;
import com.example.demologin.dto.response.QuestionTypeResponse;
import com.example.demologin.entity.QuestionType;
import com.example.demologin.exception.exceptions.ConflictException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.questiontype.IQuestionTypeMapper;
import com.example.demologin.repository.QuestionTypeRepository;
import com.example.demologin.service.QuestionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionTypeServiceImpl implements QuestionTypeService {
    private final QuestionTypeRepository questionTypeRepository;
    private final IQuestionTypeMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionTypeResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionTypeRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionTypeResponse get(Long id) {
        QuestionType questionType = questionTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question Type not found with id: " + id));
        return mapper.toResponse(questionType);
    }

    @Override
    @Transactional
    public QuestionTypeResponse create(QuestionTypeCreateRequest req) {
        // Check if description already exists
        if (questionTypeRepository.findByDescriptionIgnoreCase(req.getDescription()).isPresent()) {
            throw new ConflictException("Question Type with description '" + req.getDescription() + "' already exists");
        }

        QuestionType questionType = QuestionType.builder()
                .description(req.getDescription())
                .build();

        QuestionType saved = questionTypeRepository.save(questionType);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public QuestionTypeResponse update(Long id, QuestionTypeUpdateRequest req) {
        QuestionType questionType = questionTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question Type not found with id: " + id));

        // Check if new description already exists (excluding current entity)
        questionTypeRepository.findByDescriptionIgnoreCase(req.getDescription())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Question Type with description '" + req.getDescription() + "' already exists");
                    }
                });

        questionType.setDescription(req.getDescription());
        QuestionType updated = questionTypeRepository.save(questionType);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        QuestionType questionType = questionTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question Type not found with id: " + id));
        questionTypeRepository.delete(questionType);
    }

    @Override
    public QuestionTypeResponse findByDescription(String description) {
        return questionTypeRepository
                .findByDescriptionIgnoreCase(description)
                .map(questionType
                        -> mapper.toResponse(questionType))
                .orElseThrow(() -> new NotFoundException("Không tìm thấy loại câu hỏi " + description));
    }
}

