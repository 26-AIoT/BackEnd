package com.AIoT.Back.controller;

import com.AIoT.Back.dto.request.StudentDtos;
import com.AIoT.Back.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 테스트용

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody StudentDtos.JoinRequest request) {
        studentService.join(request);
        return ResponseEntity.ok("학생 등록 완료");
    }
}