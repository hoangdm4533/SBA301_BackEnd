package com.example.demologin.entity;


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
    private Long id;

    @Column(name = "question_text")
    private String questionText;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "question_level")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "type")
    private QuestionType type;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @OneToMany(mappedBy="question", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    public void addOption(Option o){ options.add(o); o.setQuestion(this); }
    public void clearOptions(){ for (var o: options) o.setQuestion(null); options.clear(); }
}

