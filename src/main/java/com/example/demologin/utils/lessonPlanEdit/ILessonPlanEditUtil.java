package com.example.demologin.utils.lessonPlanEdit;

import com.example.demologin.entity.LessonPlanEdit;

import java.util.List;

public interface ILessonPlanEditUtil {
    String applyEdits(String baseContent, List<LessonPlanEdit> edits);
}
