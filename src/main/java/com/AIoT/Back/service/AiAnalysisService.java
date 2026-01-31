package com.AIoT.Back.service;

import com.AIoT.Back.domain.*;
import com.AIoT.Back.domain.constant.AttendanceStatus;
import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiAnalysisService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ConcentrationLogRepository concentrationLogRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void processAiData(AiDtos data) {
        // 1. 벡터 데이터 유효성 검사
        // ★ [주의] DTO 필드명이 vec라면 getVec(), vector라면 getVector()로 수정하세요.
        List<Double> rawVec = data.getVector();

        if (rawVec == null || rawVec.isEmpty()) {
            System.out.println("서비스: 벡터 데이터가 비어있습니다.");
            return;
        }

        // 2. 점수와 벡터 분리
        // (마지막 값은 점수, 나머지는 얼굴 벡터)
        double currentScore = rawVec.get(rawVec.size() - 1);
        List<Double> inputVector = rawVec.subList(0, rawVec.size() - 1);

        // 3. 학생 찾기 (전교생 비교)
        Student matchedStudent = findGlobalBestMatchStudent(inputVector);

        if (matchedStudent == null) {
            System.out.println("일치하는 학생을 찾을 수 없습니다. (유사도 0.7 미만)");
            return;
        }

        // 4. 수강 중인 방(수업) 찾기
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudent(matchedStudent);

        if (enrollments.isEmpty()) {
            System.out.println("⚠️ 학생은 찾았으나, 수강신청된 수업이 없습니다: " + matchedStudent.getName());
            return;
        }

        // ★ 현재는 '첫 번째' 수업으로 가정 (추후 시간표 로직이 필요할 수 있음)
        Room room = enrollments.get(0).getRoom();

        // 5. 출석 처리
        boolean isPresent = attendanceRepository.existsByRoomAndStudentAndAttendanceDate(
                room, matchedStudent, LocalDate.now());

        if (!isPresent) {
            Attendance newAtt = new Attendance(room, matchedStudent, AttendanceStatus.PRESENT);
            attendanceRepository.save(newAtt);
            System.out.println("✅ 출석 인정 완료: " + matchedStudent.getName() + " (" + room.getRoomName() + ")");
        }

        // 6. 집중도 로그 저장 (점수 0.1 이하는 0 처리)
        double finalScore = (currentScore > 0.1) ? currentScore : 0.0;

        ConcentrationLog log = ConcentrationLog.builder()
                .room(room)
                .student(matchedStudent)
                .score(finalScore)
                .timestamp(LocalDateTime.now())
                .build();

        concentrationLogRepository.save(log);
        System.out.println("📊 [" + matchedStudent.getName() + "] 집중도 저장 완료: " + finalScore);
    }

    // 전교생 대상 유사도 비교 로직
    private Student findGlobalBestMatchStudent(List<Double> inputVector) {
        List<Student> allStudents = studentRepository.findAll();
        Student bestStudent = null;
        double maxSimilarity = -1.0;

        for (Student student : allStudents) {
            // 얼굴 데이터 없는 학생 건너뛰기
            if (student.getFaceVector() == null || student.getFaceVector().isEmpty()) continue;

            List<Double> dbVector = student.getFaceVector();

            // ★ [최적화] 벡터 차원(개수)이 다르면 계산하지 않고 건너뜀 (예: 128개 vs 127개)
            if (inputVector.size() != dbVector.size()) continue;

            double similarity = calculateCosineSimilarity(inputVector, dbVector);

            // 유사도가 0.7 이상인 사람 중 최고점 찾기
            if (similarity > 0.7 && similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestStudent = student;
            }
        }
        return bestStudent;
    }

    // 코사인 유사도 계산
    private double calculateCosineSimilarity(List<Double> v1, List<Double> v2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }

        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}