package com.example.demologin.service.impl;

import com.example.demologin.dto.request.plan.PlanRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.PlanResponse;
import com.example.demologin.entity.Plan;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public PlanResponse create(PlanRequest request) {
        Plan plan = Plan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(planRepository.save(plan));
    }

    @Override
    public PlanResponse update(Long id, PlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());

        return mapToResponse(planRepository.save(plan));
    }

    @Override
    public boolean delete(Long id) {
        if (!planRepository.existsById(id)) {
            throw new IllegalArgumentException("Plan not found");
        }
        planRepository.deleteById(id);
        return true;
    }

    @Override
    public PlanResponse getById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        return mapToResponse(plan);
    }

    @Override
    public PageResponse<PlanResponse> getAllPaged(Pageable pageable) {
        Page<PlanResponse> page = planRepository.findAll(pageable)
                .map(this::mapToResponse);
        return new PageResponse<>(page);
    }

    @Override
    public List<PlanResponse> getAll() {
        return planRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }


    private PlanResponse mapToResponse(Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationDays(plan.getDurationDays())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
