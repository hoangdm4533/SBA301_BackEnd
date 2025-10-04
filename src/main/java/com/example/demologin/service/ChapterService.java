package com.example.demologin.service;


import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterService {

    ChapterResponse create(ChapterRequest request);

    ChapterResponse update(Long id, ChapterRequest request);

    void delete(Long id);

    ChapterResponse getById(Long id);

    Page<ChapterResponse> getAll(Pageable pageable);
}
