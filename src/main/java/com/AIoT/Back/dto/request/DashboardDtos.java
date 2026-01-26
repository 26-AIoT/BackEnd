package com.AIoT.Back.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DashboardDtos {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentStatus {
        private Long studentId;
        private String name;
        private String studentNumber;

        private boolean isPresent;   // 출석 여부
        private Double currentScore; // 현재 점수
        private String statusMessage; // 상태 메시지
    }
}