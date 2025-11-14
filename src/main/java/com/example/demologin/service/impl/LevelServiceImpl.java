package com.example.demologin.service.impl;

import com.example.demologin.dto.request.level.LevelRequest;
import com.example.demologin.dto.response.LevelResponse;
import com.example.demologin.entity.Level;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.ExamRepository;
import com.example.demologin.repository.LevelRepository;
import com.example.demologin.service.LevelService;
import com.example.demologin.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final AccountUtils accountUtils;
    private final ExamRepository examRepository;

    private LevelResponse mapToResponse(Level level) {
        return LevelResponse.builder()
                .id(level.getId())
                .difficulty(level.getDifficulty())
                .build();
    }

    @Override
    public LevelResponse createLevel(LevelRequest request) {
        if (levelRepository.existsByDifficultyIgnoreCase(request.getDifficulty())) {
            throw new IllegalArgumentException("Level với tên này đã tồn tại");
        }

        Level level = Level.builder()
                .difficulty(request.getDifficulty())
                .score(request.getScore())
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
    public Page<LevelResponse> getAllLevels(int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return levelRepository.findAll(pageable)
                .map(this::mapToResponse);
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
        level.setDifficulty(request.getDifficulty());
        level.setScore(request.getScore());

        Level updated = levelRepository.save(level);
        return mapToResponse(updated);
    }

    @Override
    public void deleteLevel(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy level với id " + id));

        long examCount = examRepository.countByStatus("PUBLISHED");
        if (examCount > 0) {
            throw new IllegalArgumentException("Không thể xóa level vì còn có " + examCount + " exam đang được sử dụng");
        }

        levelRepository.delete(level);
    }

    @Override
    public LevelResponse getByDifficulty(String difficulty) {
        return levelRepository.findByDifficulty(difficulty)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy độ khó" + difficulty));
    }

}