package com.example.demologin.initializer.components;

import com.example.demologin.entity.Grade;
import com.example.demologin.entity.Subject;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeDataInitializer  {
    GradeRepository gradeRepository;
    SubjectRepository subjectRepository;


    public void initGrade() throws Exception {
        if (gradeRepository.count() == 0) {
            // Giả sử bạn đã có Subject trong DB
            Subject math = Subject
                    .builder()
                    .subjectName("Math")
                    .build();
             Subject objectMath =subjectRepository.save(math);

            Grade grade1 = Grade.builder()
                    .gradeNumber(10)
                    .description("Grade 10")
                    .subject(objectMath)
                    .build();

            Grade grade2 = Grade.builder()
                    .gradeNumber(11)
                    .description("Grade 11")
                    .subject(objectMath)
                    .build();
            Grade grade3 = Grade.builder()
                    .gradeNumber(12)
                    .description("Grade 12")
                    .subject(objectMath)
                    .build();

            gradeRepository.save(grade1);
            gradeRepository.save(grade2);
            gradeRepository.save(grade3);

            System.out.println("Initialized Grades data.");
        }
    }
}
