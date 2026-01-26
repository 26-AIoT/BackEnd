package com.AIoT.Back.dto.request;

import lombok.Data;

import java.util.List;
// 테스트용(postman으로 테스트하기위해서 만든거 실제로는 이미 저장되어있음)
public class StudentDtos {
    @Data
    public static class JoinRequest {
        private String name;
        private String studentNumber; // 학번
        private List<Double> faceVector; // 테스트용 얼굴 벡터
    }
}
