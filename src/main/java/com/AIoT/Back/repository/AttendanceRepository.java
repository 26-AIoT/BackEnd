package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Attendance;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 오늘 + 이 방 + 학생의 출석기록이 있는지 확인
    boolean existsByRoomAndStudentAndAttendanceDate(Room room, Student student, LocalDate date);
}
