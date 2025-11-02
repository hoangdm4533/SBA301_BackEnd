package com.example.demologin.serviceImpl;


import com.example.demologin.dto.request.matrix.MatrixDetailRequest;
import com.example.demologin.dto.request.matrix.MatrixRequest;
import com.example.demologin.dto.response.matrix.MatrixDetailResponse;
import com.example.demologin.dto.response.matrix.MatrixResponse;
import com.example.demologin.entity.*;
import com.example.demologin.repository.*;
import com.example.demologin.service.MatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatrixServiceImpl implements MatrixService {
    private final MatrixRepository matrixRepository;
    private final MatrixDetailRepository matrixDetailRepository;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public MatrixResponse createMatrix(MatrixRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Matrix matrix = Matrix.builder()
                .title(request.getTitle())
                .totalQuestion(request.getTotalQuestion())
                .totalScore(request.getTotalScore())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();

        Matrix savedMatrix = matrixRepository.save(matrix);

        // Create matrix details
        List<MatrixDetail> details = request.getDetails().stream()
                .map(detail -> createMatrixDetail(detail, savedMatrix))
                .collect(Collectors.toList());

        savedMatrix.setDetails(details);
        matrixDetailRepository.saveAll(details);
        return mapToResponse(savedMatrix);
    }

    @Override
    public Page<MatrixResponse> getAllMatrices(Pageable pageable) {
        return matrixRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public MatrixResponse getMatrixById(Long id) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        return mapToResponse(matrix);
    }

    @Override
    @Transactional
    public MatrixResponse updateMatrix(Long id, MatrixRequest request) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrix not found"));

        matrix.setTotalQuestion(request.getTotalQuestion());
        matrix.setTotalScore(request.getTotalScore());
        matrix.setUpdatedAt(LocalDateTime.now());

        // Update details
        matrixDetailRepository.deleteAll(matrix.getDetails());
        List<MatrixDetail> newDetails = request.getDetails().stream()
                .map(detail -> createMatrixDetail(detail, matrix))
                .collect(Collectors.toList());
        matrix.setDetails(newDetails);
        matrixDetailRepository.saveAll(newDetails);

        return mapToResponse(matrixRepository.save(matrix));
    }

    @Override
    @Transactional
    public void deleteMatrix(Long id) {
        Matrix matrix = matrixRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrix not found"));
        matrix.setStatus("INACTIVE");
        matrix.setUpdatedAt(LocalDateTime.now());
        matrixRepository.save(matrix);
    }

    private MatrixDetail createMatrixDetail(MatrixDetailRequest detail, Matrix matrix) {
        Level level = levelRepository.findById(detail.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found"));
        Lesson lesson = lessonRepository.findById(detail.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        return MatrixDetail.builder()
                .totalQuestions(detail.getTotalQuestions())
                .level(level)
                .lesson(lesson)
                .matrix(matrix)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private MatrixResponse mapToResponse(Matrix matrix) {
//

        List<MatrixDetailResponse> detailResponses = matrix.getDetails().stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
//        response.setDetails(detailResponses);

        return MatrixResponse.builder()
                .id(matrix.getId())
                .title(matrix.getTitle())
                .totalQuestion(matrix.getTotalQuestion())
                .totalScore(matrix.getTotalScore())
                .createdAt(matrix.getCreatedAt())
                .updatedAt(matrix.getUpdatedAt())
                .status(matrix.getStatus())
                .userName(matrix.getUser().getUsername())
                .details(detailResponses)
                .build();
    }

    private MatrixDetailResponse mapToDetailResponse(MatrixDetail detail) {
        return MatrixDetailResponse.builder()
                .id(detail.getId())
                .levelDescription(detail.getLevel().getDescription())
                .lessonName(detail.getLesson().getLessonName())
                .totalQuestions(detail.getTotalQuestions())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .build();
    }
}
