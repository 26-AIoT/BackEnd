package com.AIoT.Back.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import com.AIoT.Back.domain.ConcentrationLog;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcentrationLogRepository extends JpaRepository<ConcentrationLog, Long> {
    // 특정 학생의 특정 방에서의 모든 기록 가져오기 (시간 순서대로)
    List<ConcentrationLog> findAllByRoomAndStudentOrderByTimestampAsc(Room room, Student student);
    // 이 학생의 가장 최신 로그 찾기 (시간 내림차순 Desc)
    Optional<ConcentrationLog> findTopByRoomAndStudentOrderByTimestampDesc(Room room, Student student);
    List<ConcentrationLog> findAllByRoomAndStudentAndTimestampAfter(Room room, Student student, LocalDateTime time);
}
