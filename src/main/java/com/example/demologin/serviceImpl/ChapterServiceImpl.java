package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.exception.exceptions.ConflictException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final GradeRepository gradeRepository;

    @Override
    public ChapterResponse create(ChapterRequest request) {
        Grade grade = gradeRepository.findById(request.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));

        Chapter chapter = Chapter.builder()
                .grade(grade)
                .name(request.getName())
                .orderNo(request.getOrderNo())
                .build();

        return mapToResponse(chapterRepository.save(chapter));
    }

    @Override
    public ChapterResponse update(Long id, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (request.getGradeId() != null) {
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));
            chapter.setGrade(grade);
        }

        chapter.setName(request.getName());
        chapter.setOrderNo(request.getOrderNo());

        return mapToResponse(chapterRepository.save(chapter));
    }

    @Override
    public boolean delete(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy chương với id " + id);
        }

        try {
            chapterRepository.deleteById(id);
            return true;
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Không thể xóa chapter do đang được sử dụng ở nơi khác");
        }
    }

    @Override
    public ChapterResponse getById(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        return mapToResponse(chapter);
    }

    @Override
    public PageResponse<ChapterResponse> getAllPaged(Pageable pageable) {
        Page<ChapterResponse> mappedPage = chapterRepository.findAll(pageable)
                .map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }

    @Override
    public List<ChapterResponse> getAll() {
        return chapterRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ChapterResponse mapToResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .gradeNumber(chapter.getGrade() != null ? chapter.getGrade().getGradeNumber() : null)
                .name(chapter.getName())
                .orderNo(chapter.getOrderNo())
                .build();
    }
}