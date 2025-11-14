package com.example.demologin.service.impl;

import com.example.demologin.dto.request.grade.GradeRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.GradeResponse;
import com.example.demologin.entity.Grade;
import com.example.demologin.mapper.chapter.IChapterMapper;
import com.example.demologin.repository.ChapterRepository;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;
    private final ChapterRepository chapterRepository;
    private final IChapterMapper chapterMapper;


    private GradeResponse mapToResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .gradeNumber(grade.getGradeNumber())
                .description(grade.getDescription())
                .build();
    }

    @Override
    public GradeResponse createGrade(GradeRequest request) {
        Grade grade = Grade.builder()
                .gradeNumber(request.getGradeNumber())
                .description(request.getDescription())
                .build();
        Grade saved = gradeRepository.save(grade);
        return mapToResponse(saved);
    }

    @Override
    public GradeResponse getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found with id " + id));
        return mapToResponse(grade);
    }

    @Override
    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GradeResponse updateGrade(Long id, GradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found with id " + id));

        grade.setGradeNumber(request.getGradeNumber());
        grade.setDescription(request.getDescription());

        Grade updated = gradeRepository.save(grade);
        return mapToResponse(updated);
    }

    @Override
    public void deleteGrade(Long id) {
        if (!gradeRepository.existsById(id)) {
            throw new IllegalArgumentException("Grade not found with id " + id);
        }
        gradeRepository.deleteById(id);
    }

    @Override
    public List<ChapterResponse> getChaptersByGradeId(Long gradeId) {
        try{
// Kiểm tra Grade tồn tại
            Grade grade = gradeRepository.findById(gradeId)
                    .orElseThrow(() -> new RuntimeException("Grade not found with id: " + gradeId));

            // Lấy danh sách Chapter thuộc Grade
            return chapterRepository.findByGrade_IdOrderByOrderNoAsc(grade.getId())
                    .stream()
                    .map(chapterMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Excetpion " + e);
            return List.of();
        }
    }
}
