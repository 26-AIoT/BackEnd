package com.AIoT.Back.service;

import com.AIoT.Back.domain.Enrollment;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import com.AIoT.Back.dto.request.StudentDtos;
import com.AIoT.Back.repository.EnrollmentRepository;
import com.AIoT.Back.repository.RoomRepository;
import com.AIoT.Back.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 테스트용 (실제에서는 안씀!)

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final EnrollmentRepository enrollmentRepository;

    public Long join(StudentDtos.JoinRequest request) {
        // 학번 중복 체크
        if (studentRepository.findByStudentNumber(request.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 학번입니다.");
        }

        //방 코드로 방 찾기 (학생이 어느 방 소속인지 확인)
        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방 코드입니다."));

        // 학생 생성 및 저장
        Student student = new Student(request.getName(), request.getStudentNumber(), request.getFaceVector());
        studentRepository.save(student);

        // 강 신청(Enrollment) 테이블에 등록 (학생 <-> 방 연결)
        // (이미 등록된 경우 중복 방지 로직이 있으면 좋음)
        if (!enrollmentRepository.existsByRoomAndStudent(room, student)) {
            Enrollment enrollment = Enrollment.createEnrollment(student, room);
            enrollmentRepository.save(enrollment);
        }

        return student.getId();
    }
}
