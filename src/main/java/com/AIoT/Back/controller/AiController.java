package com.AIoT.Back.controller;

import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.service.AiAnalysisService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper;

    @PostMapping("/data")
    public ResponseEntity<String> receiveMobiusData(@RequestBody String rawJson) {
        try {
            // 1. 빈 데이터 방어
            if (rawJson == null || rawJson.isEmpty()) {
                System.out.println("⚠️ [Warning] Received Empty Data");
                return ResponseEntity.badRequest().body("Empty Body");
            }

            // 2. 데이터가 잘려서 왔는지 확인 (방어 로직)
            String trimmedJson = rawJson.trim();
            if (!trimmedJson.endsWith("}")) {
                // 에러 분석을 위해 끊긴 데이터의 길이와 끝부분을 출력
                System.out.println("⛔ [Data Truncated] JSON closing brace '}' missing.");
                System.out.println("   - Length: " + trimmedJson.length());
                System.out.println("   - Last 50 chars: " + trimmedJson.substring(Math.max(0, trimmedJson.length() - 50)));
                return ResponseEntity.ok("ignored");
            }

            // 3. JSON 파싱
            JsonNode rootNode = objectMapper.readTree(trimmedJson);
            JsonNode vecNode = rootNode.findValue("vec");

            if (vecNode == null) {
                System.out.println("⚠️ [Skip] 'vec' key not found in JSON");
                return ResponseEntity.badRequest().body("'vec' key not found");
            }

            List<Double> vectorData;

            // ★ [핵심 수정] 문자열로 왔는지, 배열로 왔는지 확인하여 처리
            if (vecNode.isTextual()) {
                // Case A: AI가 문자열로 압축해서 보낸 경우 (예: "[0.1, 0.2, ...]")
                String vecString = vecNode.asText();
                Double[] vectorArray = objectMapper.readValue(vecString, Double[].class);
                vectorData = Arrays.asList(vectorArray);
                System.out.println("✅ [Success] String-Type Vector Parsed");

            } else if (vecNode.isArray()) {
                // Case B: 기존처럼 배열로 보낸 경우 (예: [0.1, 0.2, ...])
                Double[] vectorArray = objectMapper.convertValue(vecNode, Double[].class);
                vectorData = Arrays.asList(vectorArray);
                System.out.println("✅ [Success] Array-Type Vector Parsed");

            } else {
                System.out.println("❌ [Invalid Format] 'vec' is neither String nor Array. Type: " + vecNode.getNodeType());
                return ResponseEntity.badRequest().body("Invalid format");
            }

            // 4. 서비스 실행
            AiDtos aiData = AiDtos.builder().vector(vectorData).build(); // Builder 패턴 활용
            aiAnalysisService.processAiData(aiData);

            aiAnalysisService.processAiData(aiData);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            // ★ [에러 출력 강화] 에러의 정체를 낱낱이 밝힙니다.
            System.out.println("🔥 [CRITICAL ERROR OCCURRED]");
            System.out.println("1. Exception Type : " + e.getClass().getName()); // 어떤 종류의 에러인지 (예: JsonParseException)
            System.out.println("2. Error Message  : " + e.getMessage());        // 에러 내용
            System.out.println("3. Stack Trace    :");
            e.printStackTrace(); // 에러가 발생한 정확한 코드 위치를 찍어줍니다.
            return ResponseEntity.badRequest().body("Error: " + e.getClass().getName() + " / " + e.getMessage());
        }
    }
}