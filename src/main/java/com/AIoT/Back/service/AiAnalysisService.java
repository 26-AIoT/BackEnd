package com.AIoT.Back.service;

import com.AIoT.Back.domain.*;
import com.AIoT.Back.domain.constant.AttendanceStatus;
import com.AIoT.Back.dto.request.AiDtos;
import com.AIoT.Back.dto.request.AiSessionDtos;
import com.AIoT.Back.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class AiAnalysisService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final ConcentrationLogRepository concentrationLogRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 라벨(device) : 세션정보
    private final Map<String, AiSessionDtos> sessionCache = new ConcurrentHashMap<>();

    // 프론트엔드에서 호출하는 "검사 요청" 메서드
    public void triggerVerification(Long studentId) {
        // 캐시를 뒤져서 해당 학생을 찾고 플래그를 true로 변경
        for (Map.Entry<String, AiSessionDtos> entry : sessionCache.entrySet()) {
            if (entry.getValue().getStudentId().equals(studentId)) {
                entry.getValue().setVerificationRequested(true);
                System.out.println("Student ID: " + studentId);
                return;
            }
        }
        System.out.println("⚠️ [검사 실패] 현재 접속 중이 아닌 학생입니다: " + studentId);
    }

    public void processAiData(AiDtos data) {
        // 1. 벡터 데이터 유효성 검사
        // ★ [주의] DTO 필드명이 vec라면 getVec(), vector라면 getVector()로 수정하세요.
        List<Double> rawVec = data.getVector();
        String trackingLabel = data.getDevice(); // AI가 보낸 라벨

        if (rawVec == null || rawVec.isEmpty()) {
            System.out.println("sevice: null vector");
            return;
        }

        // 2. 점수와 벡터 분리
        // (마지막 값은 점수, 나머지는 얼굴 벡터)
        double currentScore = rawVec.get(rawVec.size() - 1);
        List<Double> inputVector = rawVec.subList(0, rawVec.size() - 1);

        Student matchedStudent = null;
        boolean needVerification = false;

        // 🚀 1. 캐시 검사 (이미 아는 라벨인가?)
        if (trackingLabel != null && sessionCache.containsKey(trackingLabel)) {
            AiSessionDtos session = sessionCache.get(trackingLabel);
            Long cachedStudentId = session.getStudentId();

            // ★ [핵심 변경] 타이머가 아니라 "요청 플래그"가 true인지 확인
            if (session.isVerificationRequested()) {
                needVerification = true; // 검사 수행 필요!
                matchedStudent = studentRepository.findById(cachedStudentId).orElse(null);
                // System.out.println("🕵️ [명령 수행] 본인 확인 시작...");
            } else {
                // 검사 요청이 없으면 그냥 프리패스 (가장 빠름)
                matchedStudent = studentRepository.findById(cachedStudentId).orElse(null);
            }

            if (matchedStudent == null) sessionCache.remove(trackingLabel);
        }

        // 🔍 2. 검증 로직 (검사 요청이 들어온 경우에만 실행)
        if (matchedStudent != null && needVerification) {
            double similarity = calculateCosineSimilarity(inputVector, matchedStudent.getFaceVector());

            if (similarity >= 0.4) {
                // ✅ 본인 맞음 -> 플래그 끄기 (다시 고속 모드로 복귀)
                sessionCache.get(trackingLabel).setVerificationRequested(false);
                System.out.println("본인 확인 완료: " + matchedStudent.getName());
            } else {
                // 🚨 본인 아님 -> 점수 0점 처리 & 플래그 끄지 않음 (맞을 때까지 계속 검사)
                System.out.println("불일치! 점수 0점 처리: " + matchedStudent.getName());
                currentScore = 0.0;
            }
        }

        // 🐢 3. 신규 접속 처리 (캐시 등록)
        if (matchedStudent == null && !sessionCache.containsKey(trackingLabel)) {
            matchedStudent = findGlobalBestMatchStudent(inputVector);
            if (matchedStudent != null && trackingLabel != null) {
                // 처음엔 검사 요청 false로 시작
                sessionCache.put(trackingLabel, new AiSessionDtos(matchedStudent.getId(), false));
                System.out.println("💾 [신규 접속] " + trackingLabel + " = " + matchedStudent.getName());
            }
        }

        if (matchedStudent == null) {
            System.out.println("일치하는 학생을 찾을 수 없습니다.");
            return;
        }

        // 4. [수정] 현재 "진행 중(Started)"인 수업 찾기
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudent(matchedStudent);
        Room activeRoom = null;

        for (Enrollment enrollment : enrollments) {
            Room r = enrollment.getRoom();
            // 방이 시작된 상태인지 확인 (isClassRunning 메서드 활용)
            if (r.isClassRunning()) {
                activeRoom = r;
                break;
            }
        }

        // 진행 중인 수업이 없으면 로그 버림
        if (activeRoom == null) {
            System.out.println("⚠️ [" + matchedStudent.getName() + "] 학생은 식별됐지만, 현재 진행 중인 수업이 없습니다. 데이터 무시.");
            return;
        }

        // 5. [수정] 출석 처리 (activeRoom 사용)
        boolean isPresent = attendanceRepository.existsByRoomAndStudentAndAttendanceDate(
                activeRoom, matchedStudent, LocalDate.now()); // activeRoom 변수 사용

        if (!isPresent) {
            attendanceRepository.save(new Attendance(activeRoom, matchedStudent, AttendanceStatus.PRESENT));
            System.out.println("✅ [" + matchedStudent.getName() + "] 출석 처리 완료 (방: " + activeRoom.getRoomName() + ")");
        }

        // 6. [수정] 집중도 로그 저장 (activeRoom 사용)
        double finalScore = (currentScore > 0.1) ? currentScore : 0.0;

        ConcentrationLog log = ConcentrationLog.builder()
                .room(activeRoom) // activeRoom 변수 사용
                .student(matchedStudent)
                .score(finalScore)
                .timestamp(LocalDateTime.now())
                .build();

        concentrationLogRepository.save(log);
        System.out.println("📊 [" + matchedStudent.getName() + "] ConcentrationLog: " + finalScore);
    }

    // 전교생 대상 유사도 비교 로직
    private Student findGlobalBestMatchStudent(List<Double> inputVector) {
        List<Student> allStudents = studentRepository.findAll();
        Student bestStudent = null;
        double maxSimilarity = -1.0;

        for (Student student : allStudents) {
            if (student.getFaceVector() == null || student.getFaceVector().isEmpty()) continue;
            List<Double> dbVector = student.getFaceVector();
            if (inputVector.size() != dbVector.size()) continue;

            double similarity = calculateCosineSimilarity(inputVector, dbVector);

            // 최초 식별 시에는 깐깐하게 (0.7 이상)
            if (similarity > 0.8 && similarity > maxSimilarity) {
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