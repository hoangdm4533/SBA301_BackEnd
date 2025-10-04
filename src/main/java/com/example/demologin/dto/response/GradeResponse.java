package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GradeResponse {
    private Long id;
    private Integer gradeNumber;

    private String description;
}
