package com.AIoT.Back.controller;
import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.dto.request.MobiusDtos;
import com.AIoT.Back.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAnalysisService aiAnalysisService;
    private final ObjectMapper objectMapper; // 생성자 주입 필요 (Lombok이 해줌)

    // Mobius 알림 수신 API
    @PostMapping("/data")
    public ResponseEntity<String> receiveMobiusData(@RequestBody MobiusDtos.Notification notification) {
        try {
            // 1. 데이터 유효성 검사 (Mobius 구조가 맞는지)
            if (notification.getSgn() == null ||
                    notification.getSgn().getNev() == null ||
                    notification.getSgn().getNev().getRep() == null ||
                    notification.getSgn().getNev().getRep().getCin() == null) {
                System.out.println("잘못된 Mobius 데이터 형식 수신됨");
                return ResponseEntity.badRequest().body("잘못된 형식");
            }

            // 알맹이(con) 꺼내기
            // Mobius는 데이터를 문자열(String) 형태로 감싸서 보냄
            // 예: "{\"roomCode\":\"abc\", \"score\":80.0 ...}"
            String contentString = notification.getSgn().getNev().getRep().getCin().getCon();
            System.out.println("Mobius 수신 데이터(con): " + contentString);

            // 3. 문자열을 자바 객체(AiDataDto)로 변환 (JSON 파싱)
            AiDtos aiData = objectMapper.readValue(contentString, AiDtos.class);

            // 4. 서비스 로직 실행 (학생 찾고, 출석/점수 저장)
            aiAnalysisService.processAiData(aiData);

            return ResponseEntity.ok("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("데이터 처리 실패: " + e.getMessage());
        }
    }
}