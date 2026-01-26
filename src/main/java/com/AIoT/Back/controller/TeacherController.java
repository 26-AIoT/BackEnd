package com.AIoT.Back.controller;

import com.AIoT.Back.domain.Teacher;
import com.AIoT.Back.dto.request.RoomDtos;
import com.AIoT.Back.dto.request.TeacherDtos;
import com.AIoT.Back.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/join")
    public ResponseEntity<String> signup(@RequestBody TeacherDtos.RequestJoin request) {
        teacherService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody TeacherDtos.RequestLogin request, HttpServletRequest httpRequest) {
        Teacher teacher = teacherService.login(request);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("TEACHER_ID", teacher.getId());

        // 프론트엔드가 선생님 이름을 알면 좋으니까 이름도 같이 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그인 성공");
        response.put("name", teacher.getName());

        return ResponseEntity.ok(response);
    }
    // 굳이 안넣어도 될거같긴함
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 4. 방 생성
    @PostMapping("/room")
    public ResponseEntity<Map<String, String>> createRoom(@RequestBody TeacherDtos.CreateRoomRequest request,
                                                          @SessionAttribute(name = "TEACHER_ID", required = false) Long teacherId) {
        if (teacherId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        // 방을 만들고 생성된 '방 코드'를 받아옴
        String roomCode = teacherService.createRoom(teacherId, request.getRoomTitle());

        // 프론트에게 방 코드 전달
        Map<String, String> response = new HashMap<>();
        response.put("message", "방 생성 성공");
        response.put("roomCode", roomCode); // 학생들에게 공유할 코드

        return ResponseEntity.ok(response);
    }
    // 첫화면에서 방목록 찾기
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDtos.RoomResponse>> getMyRooms (
            @SessionAttribute(name = "TEACHER_ID", required = false) Long teacherId
    ) {
        if (teacherId == null) {
            return ResponseEntity.status(401).build();
        }

        List<RoomDtos.RoomResponse> rooms = teacherService.getMyRooms(teacherId);

        return ResponseEntity.ok(rooms);
    }
}