package com.example.demologin.service.impl;

import com.example.demologin.dto.request.lesson_plan.LessonPlanRequest;
import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.LessonPlanEditRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.LessonPlanService;
import com.example.demologin.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonPlanServiceImpl implements LessonPlanService {
    private final LessonPlanRepository lessonPlanRepo;
//    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final ObjectStorageService storageService; // Sử dụng MinIO
    private final LessonPlanEditRepository lessonPlanEditRepo;


    @Override
    public LessonPlanResponse createLessonPlan(LessonPlanRequest req) {
//        User teacher = userRepo.findById(req.getTeacherId())
//                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        Grade grade = gradeRepo.findById(req.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        // 1. Tạo lesson plan (chưa có filePath)
        LessonPlan plan = LessonPlan.builder()
//                .teacher(teacher)
                .grade(grade)
                .title(req.getTitle())
                .content(null) // content sẽ được lưu trong MinIO
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        LessonPlan saved = lessonPlanRepo.save(plan);

        // 2. BE tự sinh filePath
        String objectKey = "lessonplan/" + saved.getId() + "/base.json";

        try {
            // 3. Upload content lên MinIO
            storageService.uploadDocument(objectKey, req.getContent());

            // 4. Update lại filePath
            saved.setFilePath(objectKey);
            lessonPlanRepo.save(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save lesson plan content to MinIO", e);
        }

        return mapToResponse(saved);
    }

    public LessonPlanResponse mapToResponse(LessonPlan plan) {
       return LessonPlanResponse.builder()
                .id(plan.getId())
                .filePath(plan.getFilePath())
                .content(plan.getContent())
                .title(plan.getTitle())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .gradeNumber(plan.getGrade().getGradeNumber())
//                .teacherName(plan.getGrade().get())
                .build();
    }

    @Override
    public boolean deleteLessonPlan(Long lessonPlanId) {
        LessonPlan plan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson Plan not found"));

        try {
            lessonPlanEditRepo.deleteByLessonPlanId(lessonPlanId);

            // 1. Xóa file khỏi MinIO (nếu có)
            if (plan.getFilePath() != null) {
                storageService.deleteDocument(plan.getFilePath());
            }


            // 2. Xóa record khỏi DB
            lessonPlanRepo.delete(plan);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete lesson plan", e);
        }
    }

    @Override
    public LessonPlanResponse findLessonPlanById(Long lessonPlanId) {
        LessonPlan plan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson Plan not found"));

        // Lấy content từ MinIO
//        String content = null;
//        if (plan.getFilePath() != null) {
//            try {
//                content = storageService.fetchDocument(plan.getFilePath());
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to load lesson plan content from MinIO", e);
//            }
//        }
//
//        plan.setContent(content);
        return mapToResponse(plan);
    }

    @Override
    public List<LessonPlanResponse> getAllLessonPlans() {
        List<LessonPlan> plans = lessonPlanRepo.findAll();
        return plans.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<LessonPlanResponse> getLessonPlans(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LessonPlan> planPage = lessonPlanRepo.findAll(pageable);

        return planPage.map(this::mapToResponse);
    }

    @Override
    public LessonPlanResponse updateLessonPlan(Long lessonPlanId, LessonPlanRequest req) {
        // 1. Lấy lesson plan theo ID
        LessonPlan lessonPlan = lessonPlanRepo.findById(lessonPlanId)
                .orElseThrow(() -> new RuntimeException("LessonPlan not found with id: " + lessonPlanId));

        // 2. Cập nhật thông tin
        lessonPlan.setGrade(gradeRepo.findById(req.getGradeId()).orElseThrow(() -> new RuntimeException("Khoông tìm thâấy grade")));
        lessonPlan.setTitle(req.getTitle());
        lessonPlan.setContent(req.getContent());
        lessonPlan.setFilePath(req.getFilePath()); // có thể null

        // 3. Lưu lại
        LessonPlan response = lessonPlanRepo.save(lessonPlan);

        // 4. Trả về response
        return mapToResponse(response);
    }

    @Override
    public byte[] exportLessonPlanToWord(Long lessonPlanId) throws IOException {
        Optional<LessonPlan> lessonPlanOpt = lessonPlanRepo.findById(lessonPlanId);
        if (!lessonPlanOpt.isPresent()) {
            throw new IllegalArgumentException("LessonPlan not found with id " + lessonPlanId);
        }

        LessonPlan lessonPlan = lessonPlanOpt.get();

        XWPFDocument doc = new XWPFDocument(); // POI 3.9, không cần close()
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Title
        XWPFParagraph title = doc.createParagraph();
        title.createRun().setText(lessonPlan.getTitle());

        // Content
        XWPFParagraph content = doc.createParagraph();
        content.createRun().setText(lessonPlan.getContent() != null ? lessonPlan.getContent() : "");

        doc.write(out);

        return out.toByteArray();
    }


}
