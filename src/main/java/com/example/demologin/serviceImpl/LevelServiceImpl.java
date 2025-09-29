package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.level.LevelRequest;
import com.example.demologin.dto.response.LevelResponse;
import com.example.demologin.entity.Level;
import com.example.demologin.entity.User;
import com.example.demologin.repository.LevelRepository;
import com.example.demologin.repository.ExamTemplateRepository;
import com.example.demologin.service.LevelService;
import com.example.demologin.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LevelServiceImpl implements LevelService {
    
    private final LevelRepository levelRepository;
    private final ExamTemplateRepository examTemplateRepository;
    private final AccountUtils accountUtils;

    private LevelResponse mapToResponse(Level level) {
        return LevelResponse.builder()
                .id(level.getId())
                .name(level.getName())
                .description(level.getDescription())
                .difficulty(level.getDifficulty())
                .status(level.getStatus())
                .minScore(level.getMinScore())
                .maxScore(level.getMaxScore())
                .createdBy(level.getCreatedBy() != null ? level.getCreatedBy().getUsername() : null)
                .updatedBy(level.getUpdatedBy() != null ? level.getUpdatedBy().getUsername() : null)
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .totalExamTemplates((int) examTemplateRepository.countByLevelAndStatus(level, "PUBLISHED"))
                .build();
    }

    @Override
    public LevelResponse createLevel(LevelRequest request) {
        if (levelRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Level với tên này đã tồn tại");
        }

        if (request.getMinScore() != null && request.getMaxScore() != null && 
            request.getMinScore() > request.getMaxScore()) {
            throw new IllegalArgumentException("Điểm tối thiểu không được lớn hơn điểm tối đa");
        }

        User currentUser = accountUtils.getCurrentUser();

        Level level = Level.builder()
                .name(request.getName())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .minScore(request.getMinScore())
                .maxScore(request.getMaxScore())
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        Level saved = levelRepository.save(level);
        return mapToResponse(saved);
    }

    @Override
    public LevelResponse getLevelById(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));
        return mapToResponse(level);
    }

    @Override
    public List<LevelResponse> getAllLevels() {
        return levelRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LevelResponse> getAllLevels(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return levelRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public List<LevelResponse> getActiveLevels() {
        return levelRepository.findAllActiveOrderByMinScore()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LevelResponse> getLevelsByDifficulty(String difficulty) {
        return levelRepository.findByDifficulty(difficulty)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LevelResponse> searchLevels(String keyword, Pageable pageable) {
        return levelRepository.findByKeyword(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public LevelResponse updateLevel(Long id, LevelRequest request) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));

        if (!level.getName().equalsIgnoreCase(request.getName()) && 
            levelRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Level với tên này đã tồn tại");
        }

        if (request.getMinScore() != null && request.getMaxScore() != null && 
            request.getMinScore() > request.getMaxScore()) {
            throw new IllegalArgumentException("Điểm tối thiểu không được lớn hơn điểm tối đa");
        }

        User currentUser = accountUtils.getCurrentUser();

        level.setName(request.getName());
        level.setDescription(request.getDescription());
        level.setDifficulty(request.getDifficulty());
        level.setMinScore(request.getMinScore());
        level.setMaxScore(request.getMaxScore());
        level.setUpdatedBy(currentUser);

        if (request.getStatus() != null) {
            level.setStatus(request.getStatus());
        }

        Level updated = levelRepository.save(level);
        return mapToResponse(updated);
    }

    @Override
    public void deleteLevel(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));

        long examTemplateCount = examTemplateRepository.countByLevelAndStatus(level, "PUBLISHED");
        if (examTemplateCount > 0) {
            throw new IllegalArgumentException("Không thể xóa level vì còn có " + examTemplateCount + " exam template đang được sử dụng");
        }

        levelRepository.delete(level);
    }

    @Override
    public void activateLevel(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));

        User currentUser = accountUtils.getCurrentUser();
        level.setStatus("ACTIVE");
        level.setUpdatedBy(currentUser);
        levelRepository.save(level);
    }

    @Override
    public void deactivateLevel(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));

        User currentUser = accountUtils.getCurrentUser();
        level.setStatus("INACTIVE");
        level.setUpdatedBy(currentUser);
        levelRepository.save(level);
    }
}