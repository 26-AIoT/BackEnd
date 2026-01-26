package com.AIoT.Back.controller;
import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAnalysisService aiAnalysisService;

    // AI 데이터 수신 API
    @PostMapping("/data")
    public ResponseEntity<String> receiveAiData(@RequestBody AiDtos aiData) {

        System.out.println("AI 데이터 수신, 방 코드: " + aiData.getRoomCode());

        // 서비스에게 일 시키기 (학생 찾고 저장해라!)
        aiAnalysisService.processAiData(aiData);

        return ResponseEntity.ok("데이터 처리 완료");
    }
}