package com.example.demologin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssaySubmissionSubmitRequest {
    @NotNull(message = "Submission ID is required")
    @JsonProperty("submission_id")
    private Long submissionId;

    @NotBlank(message = "Answer is required")
    @JsonProperty("answer")
    private String answer;

    @JsonProperty("image_urls")
    private List<String> imageUrls;
}
