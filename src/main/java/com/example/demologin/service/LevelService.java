package com.example.demologin.service;

import com.example.demologin.dto.request.level.LevelRequest;
import com.example.demologin.dto.response.LevelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LevelService {
    LevelResponse createLevel(LevelRequest request);
    LevelResponse getLevelById(Long id);
    List<LevelResponse> getAllLevels();
    Page<LevelResponse> getAllLevels(int page, int size, String sortBy, String sortDir);
    Page<LevelResponse> searchLevels(String keyword, Pageable pageable);
    LevelResponse updateLevel(Long id, LevelRequest request);
    void deleteLevel(Long id);
    LevelResponse getByDifficulty(String difficulty);
}