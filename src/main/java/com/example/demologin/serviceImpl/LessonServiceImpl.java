package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.entity.Chapter;
import com.example.demologin.entity.Lesson;
import com.example.demologin.mapper.lesson.ILessonMapper;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.LessonRepository;
import com.example.demologin.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final ILessonMapper lessonMapper;

    @Override
    public LessonResponse createLesson(LessonRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        Lesson lesson = lessonMapper.toEntity(request, chapter);
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll()
                .stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        boolean updated = false;

        // Cập nhật lessonName nếu thay đổi
        if (request.getLessonName() != null && !request.getLessonName().equals(lesson.getLessonName())) {
            lesson.setLessonName(request.getLessonName());
            updated = true;
        }

        // Cập nhật descriptions nếu thay đổi
        if (request.getDescriptions() != null && !request.getDescriptions().equals(lesson.getDescriptions())) {
            lesson.setDescriptions(request.getDescriptions());
            updated = true;
        }

        // Cập nhật chapter nếu thay đổi
        if (request.getChapterId() != null &&
                (lesson.getChapter() == null || !request.getChapterId().equals(lesson.getChapter().getId()))) {
            Chapter chapter = chapterRepository.findById(request.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));
            lesson.setChapter(chapter);
            updated = true;
        }

        // Chỉ save nếu có thay đổi
        if (updated) {
            lesson = lessonRepository.save(lesson);
        }

        return lessonMapper.toResponse(lesson);
    }

    @Override
    public boolean deleteLesson(Long id) {
        try{
            if (!lessonRepository.existsById(id)) {
                throw new RuntimeException("Lesson not found");
            }
            lessonRepository.deleteById(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonResponse> getLessonsPage(Pageable pageable) {
        return lessonRepository.findAll(pageable)
                .map(lessonMapper::toResponse);
    }

    @Override
    public List<LessonResponse> getLessonsByChapterId(Long chapterId) {
        try{
            // Kiểm tra chapter tồn tại
            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));

            // Lấy danh sách bài học theo chapter
            return lessonRepository.findByChapter_IdOrderByIdAsc(chapter.getId())
                    .stream()
                    .map(lessonMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }
}
