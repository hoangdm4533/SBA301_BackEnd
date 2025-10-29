package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import com.example.demologin.repository.LessonPlanEditRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.LessonPlanCompactionService;
import com.example.demologin.service.LessonPlanStorageService;
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
    private final LessonPlanStorageService storageService;

    public LessonPlanCompactionServiceImpl(LessonPlanRepository lessonPlanRepo,
                                       LessonPlanEditRepository lessonPlanEditRepo,
                                           LessonPlanStorageService storageService) {
        this.lessonPlanRepo = lessonPlanRepo;
        this.lessonPlanEditRepo = lessonPlanEditRepo;
        this.storageService = storageService;
    }

    /**
     * Thực hiện compaction khi giáo viên bấm Save
     */
    @Transactional
    @Override
    public void compactLessonPlan(Long lessonPlanId) {
        LessonPlan lessonPlan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found: " + lessonPlanId));

        String objectKey = lessonPlan.getFilePath();
        try {
            // 1. Lấy base content từ MinIO
            String baseContent = storageService.downloadContent(objectKey);

            // 2. Lấy edits từ MySQL
            List<LessonPlanEdit> edits = lessonPlanEditRepo.findByLessonPlanIdOrderByCreatedAtAsc(lessonPlanId);

            // 3. Merge edits vào baseContent
            String mergedContent = applyEdits(baseContent, edits);

            // 4. Upload content mới lên MinIO
            storageService.uploadContent(objectKey, mergedContent);

            // 5. Update DB metadata
            lessonPlan.setUpdatedAt(LocalDateTime.now());
            lessonPlanRepo.save(lessonPlan);

            // 6. Xoá edits cũ
            lessonPlanEditRepo.deleteAll(edits);

            System.out.println("Compaction thành công cho lessonPlanId=" + lessonPlanId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi compaction cho lessonPlanId=" + lessonPlanId, e);
        }
    }

    /**
     * Demo apply edit đơn giản:
     * Edit JSON có thể là { "operation":"append", "text":"abc" }
     * hoặc { "operation":"replace", "text":"new text" }
     */
    private String applyEdits(String baseContent, List<LessonPlanEdit> edits) {
        StringBuilder result = new StringBuilder(baseContent);
        ObjectMapper mapper = new ObjectMapper();

        for (LessonPlanEdit edit : edits) {
            try {
                JsonNode node = mapper.readTree(edit.getOperation());
                String action = node.get("action").asText(); // đúng key là "action"

                switch (action) {
                    case "insert" -> {
                        int pos = node.has("pos") ? node.get("pos").asInt() : result.length();
                        String ch = node.has("char") ? node.get("char").asText() : "";

                        // Bảo vệ giới hạn
                        if (pos < 0) pos = 0;
                        if (pos > result.length()) pos = result.length();

                        result.insert(pos, ch);
                    }

                    case "delete" -> {
                        int start = node.has("start") ? node.get("start").asInt() : -1;
                        int end = node.has("end") ? node.get("end").asInt() : -1;

                        if (start >= 0 && end > start && end <= result.length()) {
                            result.delete(start, end);
                        } else {
                            System.err.println("Invalid delete range for edit: " + edit.getId());
                        }
                    }

                    default -> System.err.println("Unknown action: " + action);
                }
            } catch (Exception ex) {
                System.err.println("Skip invalid edit: " + edit.getId() + " due to error: " + ex.getMessage());
            }
        }

        return result.toString();
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
