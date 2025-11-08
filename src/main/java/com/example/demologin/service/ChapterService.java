package com.example.demologin.service;


import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChapterService {

    ChapterResponse create(ChapterRequest request);

    ChapterResponse update(Long id, ChapterRequest request);

    boolean delete(Long id);

    ChapterResponse getById(Long id);

    PageResponse<ChapterResponse> getAllPaged(Pageable pageable);

    List<ChapterResponse> getAll();
}
