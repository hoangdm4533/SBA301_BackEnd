package com.example.demologin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCardResponse {
    private Long users;
    private Long plans;
    private Long transactions;
}
