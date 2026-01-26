package com.AIoT.Back.service;

import com.AIoT.Back.domain.*;
import com.AIoT.Back.domain.constant.AttendanceStatus;
import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.repository.*;
import com.AIoT.Back.utils.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AiAnalysisService {
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final AttendanceRepository attendanceRepository;
    private final ConcentrationLogRepository concentrationLogRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void processAiData(AiDtos data) {
        // 방 찾기 (roomCode로)
        Room room = roomRepository.findByRoomCode(data.getRoomCode())
                .orElse(null);

        if (room == null) {
            System.out.println("존재하지 않는 방 코드: " + data.getRoomCode());
            return;
        }

        // 학생 식별
        Student student = findStudentByVector(data.getVector());
        if (student == null) {
            System.out.println("등록되지 않은 학생 얼굴 감지됨");
            return;
        }
        // 이거는 추후에 확장가능성 (현재는 그냥 DB에 우리정보만 넣는걸로!)
        if (!enrollmentRepository.existsByRoomAndStudent(room, student)) {
            Enrollment enrollment = Enrollment.createEnrollment(student, room);
            enrollmentRepository.save(enrollment);
            System.out.println("새 수강생 자동 등록: " + student.getName());
        }

        // 출석 체크 (오늘 첫 기록이면 출석 처리)
        boolean isAttended = attendanceRepository.existsByRoomAndStudentAndAttendanceDate(
                room, student, LocalDate.now());

        if (!isAttended) {
            Attendance attendance = new Attendance(room, student, AttendanceStatus.PRESENT);
            attendanceRepository.save(attendance);
            System.out.println("출석 인정 완료: " + student.getName());
        }

        // 집중도 로그 저장
        ConcentrationLog log = new ConcentrationLog(room, student, data.getScore());
        concentrationLogRepository.save(log);

        System.out.println("데이터 저장 완료 [" + student.getName() + "]: 점수=" + data.getScore());

    }
    // 벡터 유사도로 학생 찾기
    private Student findStudentByVector(List<Double> inputVector) {
        List<Student> allStudents = studentRepository.findAll();
        Student bestMatch = null;
        double maxSimilarity = 0.0;

        for (Student s : allStudents) {
            // DB에 저장된 벡터 꺼내기 (Converter가 자동으로 List<Double>로 바꿔줌)
            List<Double> dbVector = s.getFaceVector();

            if (dbVector == null || dbVector.isEmpty()) continue;

            double similarity = VectorUtils.consineSimilarity(inputVector, dbVector);

            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = s;
            }
        }

        // 유사도 75% (0.70) 이상이어야 본인으로 인정
        return (maxSimilarity > 0.70) ? bestMatch : null;
    }
}
