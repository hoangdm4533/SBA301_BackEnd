package com.example.demologin.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionId implements Serializable {
    private Long exam;
    private Long question;
}
