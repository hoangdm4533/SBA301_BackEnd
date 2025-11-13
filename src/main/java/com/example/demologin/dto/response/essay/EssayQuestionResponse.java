package com.example.demologin.dto.response.essay;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demologin.enums.QuestionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssayQuestionResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("grade_id")
    private Long gradeId;

    @JsonProperty("grade_number")
    private Integer gradeNumber;

    @JsonProperty("chapter_id")
    private Long chapterId;

    @JsonProperty("chapter_name")
    private String chapterName;

    @JsonProperty("lesson_id")
    private Long lessonId;

    @JsonProperty("lesson_name")
    private String lessonName;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("rubric")
    private String rubric;

    @JsonProperty("time_limit_minutes")
    private Integer timeLimitMinutes;

    @JsonProperty("max_score")
    private Integer maxScore;

    @JsonProperty("status")
    private QuestionStatus status;

    @JsonProperty("created_by")
    private String createdBy; // Teacher name

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("attachments")
    private List<EssayAttachmentResponse> attachments;

    @JsonProperty("attachment_urls")
    private List<String> attachmentUrls;
}
