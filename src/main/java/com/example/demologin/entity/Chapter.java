package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderNo;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @OneToMany(mappedBy = "chapter")
    private List<Lesson> lessons;
}

