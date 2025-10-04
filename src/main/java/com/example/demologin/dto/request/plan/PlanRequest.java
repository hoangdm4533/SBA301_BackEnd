package com.example.demologin.dto.request.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request để tạo hoặc cập nhật gói Plan")
public class PlanRequest {

    @Schema(description = "Tên gói", example = "Premium Plan")
    private String name;

    @Schema(description = "Mô tả gói", example = "Truy cập tất cả khóa học và nội dung đặc biệt")
    private String description;

    @Schema(description = "Giá gói", example = "199.99")
    private Double price;

    @Schema(description = "Thời hạn sử dụng (ngày)", example = "30")
    private Integer durationDays;
}