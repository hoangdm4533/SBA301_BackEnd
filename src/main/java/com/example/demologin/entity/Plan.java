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
@Table(name = "plans")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    Double price;
    Integer durationDays;
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "plan")
    List<Subscription> subscriptions;
}

