package com.example.demologin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterResponse {
    private Long id;
    private Long lessonPlanId;
    private String lessonPlanName;
    private String name;
    private Integer orderNo;
}
