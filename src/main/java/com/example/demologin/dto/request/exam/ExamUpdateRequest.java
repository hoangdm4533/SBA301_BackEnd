package com.example.demologin.dto.request.exam;

import lombok.Data;

@Data
public class ExamUpdateRequest {
    private String title;
    private String description;
    private String status; // DRAFT|PUBLISHED|ARCHIVED etc.
}
