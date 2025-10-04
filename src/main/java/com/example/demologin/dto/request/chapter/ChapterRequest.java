package com.example.demologin.dto.request.chapter;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterRequest {
    private Long lessonPlanId;
    private String name;
    private Integer orderNo;
}