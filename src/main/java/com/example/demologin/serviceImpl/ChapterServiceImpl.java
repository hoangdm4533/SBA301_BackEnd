package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final LessonPlanRepository lessonPlanRepository;

    @Override
    public ChapterResponse create(ChapterRequest request) {
        LessonPlan lessonPlan = lessonPlanRepository.findById(request.getLessonPlanId())
                .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));

        Chapter chapter = Chapter.builder()
                .lessonPlan(lessonPlan)
                .name(request.getName())
                .orderNo(request.getOrderNo())
                .build();

        return mapToResponse(chapterRepository.save(chapter));
    }

    @Override
    public ChapterResponse update(Long id, ChapterRequest request) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (request.getLessonPlanId() != null) {
            LessonPlan lessonPlan = lessonPlanRepository.findById(request.getLessonPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));
            chapter.setLessonPlan(lessonPlan);
        }

        chapter.setName(request.getName());
        chapter.setOrderNo(request.getOrderNo());

        return mapToResponse(chapterRepository.save(chapter));
    }

    @Override
    public void delete(Long id) {
        if (!chapterRepository.existsById(id)) {
            throw new IllegalArgumentException("Chapter not found");
        }
        chapterRepository.deleteById(id);
    }

    @Override
    public ChapterResponse getById(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        return mapToResponse(chapter);
    }

    @Override
    public Page<ChapterResponse> getAll(Pageable pageable) {
        return chapterRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private ChapterResponse mapToResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .lessonPlanId(chapter.getLessonPlan() != null ? chapter.getLessonPlan().getId() : null)
                .lessonPlanName(chapter.getLessonPlan() != null ? chapter.getLessonPlan().getTitle() : null)
                .name(chapter.getName())
                .orderNo(chapter.getOrderNo())
                .build();
    }
}