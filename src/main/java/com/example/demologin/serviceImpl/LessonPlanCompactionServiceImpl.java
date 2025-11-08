package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import com.example.demologin.repository.LessonPlanEditRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.LessonPlanCompactionService;
import com.example.demologin.service.LessonPlanStorageService;
import com.example.demologin.utils.lessonPlanEdit.ILessonPlanEditUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonPlanCompactionServiceImpl implements LessonPlanCompactionService {
    private final LessonPlanRepository lessonPlanRepo;
    private final LessonPlanEditRepository lessonPlanEditRepo;
    private final LessonPlanStorageService storageService;
    private final ILessonPlanEditUtil lessonPlanEditUtil;

//    public LessonPlanCompactionServiceImpl(LessonPlanRepository lessonPlanRepo,
//                                       LessonPlanEditRepository lessonPlanEditRepo,
//                                           LessonPlanStorageService storageService) {
//        this.lessonPlanRepo = lessonPlanRepo;
//        this.lessonPlanEditRepo = lessonPlanEditRepo;
//        this.storageService = storageService;
//    }

    /**
     * Thực hiện compaction khi giáo viên bấm Save
     */
    @Transactional
    @Override
    public void compactLessonPlan(Long lessonPlanId) {
        // 1️⃣ Lấy LessonPlan hiện tại
        LessonPlan lessonPlan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found: " + lessonPlanId));

        try {
            // 2️⃣ Lấy nội dung hiện tại trong DB
            String baseContent = lessonPlan.getContent();
            if (baseContent == null) {
                baseContent = "";
            }

            // 3️⃣ Lấy tất cả edits từ DB
            List<LessonPlanEdit> edits = lessonPlanEditRepo.findByLessonPlanIdOrderByCreatedAtAsc(lessonPlanId);
            if (edits.isEmpty()) {
                System.out.println("[Compact] Không có edit nào cho lessonPlanId=" + lessonPlanId);
                return;
            }

            // 4️⃣ Merge edits vào baseContent
            String mergedContent = lessonPlanEditUtil.applyEdits(baseContent, edits);

            // 5️⃣ Cập nhật LessonPlan trong DB
            lessonPlan.setContent(mergedContent);
            lessonPlan.setUpdatedAt(LocalDateTime.now());
            lessonPlanRepo.save(lessonPlan);

            // 6️⃣ Xoá các bản ghi edit sau khi merge thành công
            lessonPlanEditRepo.deleteAllInBatch(edits);

            System.out.printf("[Compact] ✅ Merge thành công %d edits cho lessonPlanId=%d%n", edits.size(), lessonPlanId);
        } catch (Exception e) {
            throw new RuntimeException("❌ Lỗi compaction cho lessonPlanId=" + lessonPlanId, e);
        }
    }

    /**
     * Demo apply edit đơn giản:
     * Edit JSON có thể là { "operation":"append", "text":"abc" }
     * hoặc { "operation":"replace", "text":"new text" }
     */
//    public String applyEdits(String baseContent, List<LessonPlanEdit> edits) {
//        StringBuilder result = new StringBuilder(baseContent);
//        ObjectMapper mapper = new ObjectMapper();
//
//        int shift = 0; // tổng thay đổi độ dài so với baseContent ban đầu
//
//        for (LessonPlanEdit edit : edits) {
//            try {
//                JsonNode node = mapper.readTree(edit.getOperation());
//                String action = node.get("action").asText();
//
//                switch (action) {
//                    case "insert" -> {
//                        int pos = node.path("pos").asInt(result.length());
//                        String ch = node.path("char").asText("");
//
//                        pos = Math.max(0, Math.min(pos + shift, result.length()));
//
//                        result.insert(pos, ch);
//
//                        shift += ch.length(); // tăng shift
//                    }
//
//                    case "delete" -> {
//                        int start = node.path("start").asInt(-1);
//                        int end = node.path("end").asInt(-1);
//
//                        start = start + shift;
//                        end = end + shift;
//
//                        if (start >= 0 && end > start && end <= result.length()) {
//                            result.delete(start, end);
//                            shift -= (end - start); // giảm shift
//                        } else {
//                            System.err.printf("Invalid delete range for edit %d: start=%d end=%d len=%d%n",
//                                    edit.getId(), start, end, result.length());
//                        }
//                    }
//
//                    default -> System.err.println("Unknown action: " + action);
//                }
//            } catch (Exception ex) {
//                System.err.println("Skip invalid edit " + edit.getId() + ": " + ex.getMessage());
//            }
//        }
//
//        return result.toString();
//    }




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
