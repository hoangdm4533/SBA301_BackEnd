package com.example.demologin.entity;


import com.example.demologin.enums.QuestionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "question_text")
    String questionText;

    @Column(name = "image_url")
    String imageUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    QuestionStatus status = QuestionStatus.ACTIVE;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "question_level")
    Level level;

    @ManyToOne
    @JoinColumn(name = "type")
    QuestionType type;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    Lesson lesson;

    @OneToMany(mappedBy="question", cascade=CascadeType.ALL, orphanRemoval=true)
    List<Option> options = new ArrayList<>();

    public void addOption(Option o){ options.add(o); o.setQuestion(this); }
    public void clearOptions(){ for (var o: options) o.setQuestion(null); options.clear(); }
}

