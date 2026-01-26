package com.AIoT.Back.service;

import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Teacher;
import com.AIoT.Back.dto.request.TeacherDtos;
import com.AIoT.Back.repository.RoomRepository;
import com.AIoT.Back.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;

    public Long signup(TeacherDtos.RequestJoin request) {
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화는 생략 (시간 부족 시 평문 저장, 여유 되면 BCrypt 사용 추천)
        Teacher teacher = new Teacher(request.getEmail(), request.getPassword(), request.getName());
        teacherRepository.save(teacher);

        return teacher.getId();
    }

    public Teacher login(TeacherDtos.RequestLogin request) {
        Teacher teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 비밀번호 일치 확인
        if (!teacher.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return teacher;
    }

    // 방 생성 (어떤 선생님이 만들었는지 매핑)
    public String createRoom(Long teacherId, String roomTitle) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("선생님 정보를 찾을 수 없습니다."));

        // 랜덤 방 코드 생성
        String roomCode = UUID.randomUUID().toString().substring(0, 8);

        // 친구가 만든 생성자 사용: Room(이름, 코드, 선생님객체)
        Room room = new Room(roomTitle, roomCode, teacher);

        roomRepository.save(room);

        return roomCode; // 프론트에 방 코드를 리턴해줘야 학생들에게 공유 가능
    }
}