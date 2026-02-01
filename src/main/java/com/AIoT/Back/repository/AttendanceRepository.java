package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Attendance;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // [기존 삭제 또는 미사용] 날짜 기준 조회
    // boolean existsByRoomAndStudentAndAttendanceDate(...);

    // ★ [추가] "이 방"에서 "이 학생"이 "기준 시간(startedAt)" 이후에 출석한 기록이 있는지 확인
    boolean existsByRoomAndStudentAndCreatedAtAfter(Room room, Student student, LocalDateTime startedAt);
}