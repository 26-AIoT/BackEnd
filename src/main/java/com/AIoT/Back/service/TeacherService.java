package com.AIoT.Back.service;

import com.AIoT.Back.domain.*;
import com.AIoT.Back.dto.request.DashboardDtos;
import com.AIoT.Back.dto.request.RoomDtos;
import com.AIoT.Back.dto.request.StudentDtos;
import com.AIoT.Back.dto.request.TeacherDtos;
import com.AIoT.Back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final ConcentrationLogRepository concentrationLogRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;

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

    // 선생님의 방 목록 조회
    public List<RoomDtos.RoomResponse> getMyRooms(Long teacherId) {
        List<Room> rooms = roomRepository.findAllByTeacherIdOrderByIdDesc(teacherId);

        return rooms.stream()
                .map(room -> {
                    // 이 방에 등록된 학생 수 세기
                    int count = enrollmentRepository.countByRoom(room);
                    return new RoomDtos.RoomResponse(room, count);
                })
                .collect(Collectors.toList());
    }

    // 실시간 대시보드 조회
    @Transactional(readOnly = true)
    public List<DashboardDtos.StudentStatus> getRoomDashboard(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        List<Enrollment> enrollments = enrollmentRepository.findAllByRoom(room);
        List<DashboardDtos.StudentStatus> dashboardList = new ArrayList<>();

        // 기준 시간: 현재로부터 5분 전
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        for (Enrollment enrollment : enrollments) {
            Student student = enrollment.getStudent();

            // 출석부 확인
            boolean isPresent = attendanceRepository.existsByRoomAndStudentAndAttendanceDate(
                    room, student, LocalDate.now());

            // 2. (if로 했는데 안되서 일단 이렇게) 출석 여부(isPresent)와 상관없이 일단 로그를 다 가져와!
            List<ConcentrationLog> recentLogs = concentrationLogRepository
                    .findAllByRoomAndStudentAndTimestampAfter(room, student, fiveMinutesAgo);

            // 가장 최신 로그 점수 확인
            ConcentrationLog lastLog = recentLogs.isEmpty() ? null : recentLogs.get(recentLogs.size() - 1);
            Double currentScore = (lastLog != null) ? lastLog.getScore() : 0.0;

            // 상태 메시지 결정
            String message = "미출석";

            // 로그가 하나라도 있으면 -> 사실상 출석한 거임! (강제로 true 처리)
            if (!recentLogs.isEmpty()) {
                isPresent = true;
            }

            if (isPresent) {
                if (recentLogs.isEmpty()) {
                    // 출석부엔 있는데 로그가 없는 경우
                    message = "출석완료";
                } else {
                    // 로그 분석
                    boolean isAway = recentLogs.stream().allMatch(log -> log.getScore() == 0.0);

                    if (isAway) message = "자리비움";
                    else if (currentScore < 50.0) message = "집중력 저하";
                    else message = "집중 중";
                }
            }

            dashboardList.add(DashboardDtos.StudentStatus.builder()
                    .studentId(student.getId())
                    .name(student.getName())
                    .studentNumber(student.getStudentNumber())
                    .isPresent(isPresent) // 로그가 있으면 true로 바뀜
                    .currentScore(currentScore)
                    .statusMessage(message)
                    .build());
        }

        return dashboardList;
    }

    // 학생 상세 기록 조회 (그래프용)
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentHistory(Long roomId, Long studentId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방 없음"));
        Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("학생 없음"));

        // 전체 로그 가져오기
        List<ConcentrationLog> logs = concentrationLogRepository.findAllByRoomAndStudentOrderByTimestampAsc(room, student);

        // 그래프용 데이터로 변환 (시간, 점수)
        List<Map<String, Object>> historyList = logs.stream().map(log -> {
            Map<String, Object> data = new HashMap<>();
            data.put("time", log.getTimestamp());
            data.put("score", log.getScore());
            return data;
        }).collect(Collectors.toList());

        // 최근 30초(또는 최근 10개) 평균 점수 계산
        // (데이터가 없으면 0.0)
        double recentAvg = logs.stream()
                    .skip(Math.max(0, logs.size() - 10)) // 끝에서 10개만 남김
                    .mapToDouble(ConcentrationLog::getScore)
                    .average()
                    .orElse(0.0);

        // 결과 맵핑
        Map<String, Object> response = new HashMap<>();
        response.put("studentName", student.getName());
        response.put("recentAverage", Math.round(recentAvg * 10) / 10.0); // 소수점 첫째자리 반올림
        response.put("history", historyList);

        return response;
    }

    // 반별 학생 명단 조회 (관리 페이지용)
    @Transactional(readOnly = true)
    public List<StudentDtos.Response> getStudentList(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        // 이 방에 수강신청한 내역(Enrollment)을 다 가져옴
        List<Enrollment> enrollments = enrollmentRepository.findAllByRoom(room);

        // Enrollment -> Student -> DTO 변환
        return enrollments.stream()
                .map(enrollment -> {
                    Student s = enrollment.getStudent();
                    // StudentDtos.Response가 (id, name, studentNumber)를 받는다고 가정
                    return new StudentDtos.Response(s.getId(), s.getName(), s.getStudentNumber());
                })
                .collect(Collectors.toList());
    }
}