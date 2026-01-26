package com.AIoT.Back.utils;

import java.util.List;

public class VectorUtils {
    // 두 벡터 사이의 코사인 유사도 계산
    // 결과값은 -1.0 ~ 1.0이 나옴
    public static double consineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA.size() != vectorB.size() || vectorA == null || vectorB == null) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        // 코사인 유사도 공식
        for (int i = 0; i < vectorA.size(); i++) {
            double valA = vectorA.get(i);
            double valB = vectorB.get(i);

            dotProduct += valA * valB;      // 분자
            normA += Math.pow(valA, 2);     // 분모 A
            normB += Math.pow(valB, 2);     // 분모 B
        }

        if (normA == 0 || normB == 0) {
            return 0.0; // 분모가 0이면 계산 X
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
