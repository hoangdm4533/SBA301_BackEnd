package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import com.example.demologin.repository.LessonPlanEditRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.LessonPlanCompactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LessonPlanCompactionServiceImpl implements LessonPlanCompactionService {
    private final LessonPlanRepository lessonPlanRepo;
    private final LessonPlanEditRepository lessonPlanEditRepo;

    public LessonPlanCompactionServiceImpl(LessonPlanRepository lessonPlanRepo,
                                       LessonPlanEditRepository lessonPlanEditRepo) {
        this.lessonPlanRepo = lessonPlanRepo;
        this.lessonPlanEditRepo = lessonPlanEditRepo;
    }

    /**
     * Thực hiện compaction khi giáo viên bấm Save
     */
    @Transactional
    @Override
    public LessonPlanResponse compactLessonPlan(Long lessonPlanId) {
        LessonPlan plan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));

        // Lấy tất cả edits
        List<LessonPlanEdit> edits = lessonPlanEditRepo.findByLessonPlanOrderByCreatedAtAsc(plan);

        if (edits.isEmpty()) {
            return mapToResponse(plan); // không có edit nào thì trả về luôn
        }

        String currentContent = plan.getContent() != null ? plan.getContent() : "";

        // Apply các edits tuần tự
        for (LessonPlanEdit edit : edits) {
            currentContent = applyOperation(currentContent, edit.getOperation());
        }

        // Update LessonPlan
        plan.setContent(currentContent);
        plan.setUpdatedAt(LocalDateTime.now());
        lessonPlanRepo.save(plan);

        // Xóa edits sau khi compact
        lessonPlanEditRepo.deleteByLessonPlan(plan);

        return mapToResponse(plan);
    }

    /**
     * Hàm apply operation đơn giản (có thể thay bằng parser JSON phức tạp hơn sau)
     */
    private String applyOperation(String content, String operationJson) {
        // Ví dụ: {"action":"insert","pos":5,"char":"A"}
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(operationJson);
            String action = node.get("action").asText();

            if ("insert".equals(action)) {
                int pos = node.get("pos").asInt();
                String ch = node.get("char").asText();
                return new StringBuilder(content).insert(pos, ch).toString();
            } else if ("delete".equals(action)) {
                int pos = node.get("pos").asInt();
                return new StringBuilder(content).deleteCharAt(pos).toString();
            } else if ("replace".equals(action)) {
                int pos = node.get("pos").asInt();
                String ch = node.get("char").asText();
                return new StringBuilder(content).replace(pos, pos + 1, ch).toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply operation: " + operationJson, e);
        }
        return content;
    }

    private LessonPlanResponse mapToResponse(LessonPlan plan) {
        LessonPlanResponse dto = new LessonPlanResponse();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setContent(plan.getContent());
        dto.setFilePath(plan.getFilePath());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
//        dto.setTeacherName(plan.getTeacher().getFullName());
//        dto.setGradeName(plan.getGrade().getName());
        return dto;
    }
}
