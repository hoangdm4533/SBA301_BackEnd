package com.example.demologin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response trả về thông tin Plan")
public class PlanResponse {

    @Schema(description = "ID của gói", example = "1")
    private Long id;

    @Schema(description = "Tên gói", example = "Premium Plan")
    private String name;

    @Schema(description = "Mô tả gói", example = "Truy cập tất cả khóa học và nội dung đặc biệt")
    private String description;

    @Schema(description = "Giá gói", example = "199.99")
    private Double price;

    @Schema(description = "Thời hạn sử dụng (ngày)", example = "30")
    private Integer durationDays;

    @Schema(description = "Thời điểm tạo", example = "2025-10-04T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Thời điểm cập nhật", example = "2025-10-04T11:15:30")
    private LocalDateTime updatedAt;
}