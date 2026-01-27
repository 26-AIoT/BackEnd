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

@Service
@RequiredArgsConstructor
@Transactional
public class AiAnalysisService {
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final AttendanceRepository attendanceRepository;
    private final ConcentrationLogRepository concentrationLogRepository;
    // enrollmentRepository는 이제 조회용으로는 안쓰고 studentRepository 쿼리로 대체함

    public void processAiData(AiDtos data) {
        // 방 찾기 (roomCode로)
        Room room = roomRepository.findByRoomCode(data.getRoomCode())
                .orElse(null);

        if (room == null) {
            System.out.println("존재하지 않는 방 코드: " + data.getRoomCode());
            return;
        }

        //  전교생이 아니라, "이 방에 등록된 학생들"만 가져오기
        List<Student> classStudents = studentRepository.findStudentsByRoomId(room.getId());

        if (classStudents.isEmpty()) {
            System.out.println("이 방에 등록된 학생이 한 명도 없습니다.");
            return;
        }

        // 3. 가져온 "우리 반 학생들" 중에서만 얼굴 비교
        Student student = findStudentByVector(data.getVector(), classStudents);

        if (student == null) {
            System.out.println("⚠️ 우리 반 학생이 아닙니다. (유사도 낮음)");
            return;
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
    private Student findStudentByVector(List<Double> inputVector, List<Student> classStudents) {
        Student bestMatch = null;
        double maxSimilarity = 0.0;

        // 전달받은 "우리 반 학생들"만 반복문 돌림
        for (Student s : classStudents) {
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
