package com.AIoT.Back.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class StudentDtos {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinRequest {
        private String name;
        private String studentNumber; // 학번
        private String roomCode; // 방 코드
        private List<Double> faceVector; // 테스트용 얼굴 벡터
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long studentId;
        private String name;
        private String studentNumber;
    }
}
