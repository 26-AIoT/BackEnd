package com.AIoT.Back.controller;

import com.AIoT.Back.dto.request.LoginReq;
import com.AIoT.Back.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/auth/join")
    public ResponseEntity<String> Join(@RequestBody LoginReq.RequestJoin req) {
        teacherService.join(req);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginReq.RequestLogin req, HttpServletRequest httpreq) {

        // 1. 서비스에서 ID/PW 검증 (실패 시 예외 발생)
        // 로그인 성공 시 선생님의 ID(PK)를 반환받음
        Long teacherId = teacherService.login(req).getId();

        // 2. 세션 생성 (기존 세션 있으면 재사용, 없으면 생성)
        HttpSession session = httpreq.getSession(true);

        // 3. 세션에 선생님 ID 저장 (이게 로그인 증명서가 됨)
        session.setAttribute("TEACHER_ID", teacherId);
        session.setMaxInactiveInterval(60 * 120); // 세션 유지 시간 120분

        return ResponseEntity.ok("로그인 성공");
    }

}
