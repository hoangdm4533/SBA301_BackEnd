package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "levels")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String difficulty;
    Double score;
}