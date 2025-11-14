package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "options")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    @Column(columnDefinition = "TEXT")
    String optionText;
    Boolean isCorrect;
}
