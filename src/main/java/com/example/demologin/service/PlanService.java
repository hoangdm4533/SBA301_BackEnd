package com.example.demologin.service;


import com.example.demologin.dto.request.plan.PlanRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.PlanResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanService {

    PlanResponse create(PlanRequest request);

    PlanResponse update(Long id, PlanRequest request);

    void delete(Long id);

    PlanResponse getById(Long id);

    PageResponse<PlanResponse> getAllPaged(Pageable pageable);

    List<PlanResponse> getAll();
}
