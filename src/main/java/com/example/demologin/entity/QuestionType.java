package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
}
