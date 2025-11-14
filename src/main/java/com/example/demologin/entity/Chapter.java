package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chapters")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    Integer orderNo;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    Grade grade;

    @OneToMany(mappedBy = "chapter")
    List<Lesson> lessons;
}

