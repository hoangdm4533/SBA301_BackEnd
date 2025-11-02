package com.example.demologin.utils.lessonPlanEdit;

import com.example.demologin.entity.LessonPlanEdit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LessonPlanEditUtil implements ILessonPlanEditUtil{
    @Override
    public String applyEdits(String baseContent, List<LessonPlanEdit> edits) {
        StringBuilder result = new StringBuilder(baseContent);
        ObjectMapper mapper = new ObjectMapper();
        int shift = 0; // tổng thay đổi độ dài so với baseContent ban đầu

        for (LessonPlanEdit edit : edits) {
            try {
                JsonNode node = mapper.readTree(edit.getOperation());
                String action = node.path("action").asText();

                switch (action) {
                    // ======================================================
                    // 🟢 INSERT
                    // ======================================================
                    case "insert" -> {
                        int pos = node.path("pos").asInt(result.length());
                        String text = node.has("text") ? node.path("text").asText("")
                                : node.path("char").asText("");
                        pos = Math.max(0, Math.min(pos + shift, result.length()));

                        result.insert(pos, text);
                        shift += text.length();
                    }

                    // ======================================================
                    // 🔴 DELETE
                    // ======================================================
                    case "delete" -> {
                        int start = node.path("start").asInt(-1);
                        int end = node.path("end").asInt(-1);
                        int pos = node.path("pos").asInt(-1);

                        // 🧩 Nếu có start & end (xóa nhiều ký tự)
                        if (start >= 0 && end > start) {
                            if (result.length() == 0) break; // content trống -> bỏ qua
                            start = Math.max(0, Math.min(start + shift, result.length()));
                            end = Math.max(start, Math.min(end + shift, result.length()));

                            if (start < end) {
                                result.delete(start, end);
                                shift -= (end - start);
                            } else {
                                System.err.printf("⚠️ Delete skipped for edit %d: invalid range start=%d end=%d len=%d%n",
                                        edit.getId(), start, end, result.length());
                            }

                            // 🧩 Nếu chỉ có pos (xóa 1 ký tự)
                        } else if (pos >= 0) {
                            if (result.length() == 0) break;
                            int realPos = Math.min(pos + shift, result.length() - 1);
                            if (realPos >= 0 && realPos < result.length()) {
                                result.deleteCharAt(realPos);
                                shift -= 1;
                            } else {
                                System.err.printf("⚠️ Delete skipped for edit %d: invalid pos=%d len=%d%n",
                                        edit.getId(), realPos, result.length());
                            }

                        } else {
                            System.err.printf("⚠️ Missing delete position info for edit %d%n", edit.getId());
                        }
                    }

                    // ======================================================
                    // 🟡 REPLACE
                    // ======================================================
                    case "replace" -> {
                        int start = node.path("start").asInt(-1);
                        int end = node.path("end").asInt(-1);
                        String newText = node.path("text").asText("");

                        start = Math.max(0, start + shift);
                        end = Math.min(result.length(), end + shift);

                        if (start >= 0 && end >= start && end <= result.length()) {
                            result.replace(start, end, newText);
                            shift += (newText.length() - (end - start));
                        } else {
                            System.err.printf("Invalid replace range for edit %d: start=%d end=%d len=%d%n",
                                    edit.getId(), start, end, result.length());
                        }
                    }

                    // ======================================================
                    default -> System.err.println("Unknown action: " + action);
                }

            } catch (Exception ex) {
                System.err.printf("Skip invalid edit %d: %s%n", edit.getId(), ex.getMessage());
            }
        }

        return result.toString();
    }
}
