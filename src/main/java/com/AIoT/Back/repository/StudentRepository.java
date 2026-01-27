package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);

    // ★ [추가] 특정 방(RoomId)에 수강 신청(Enrollment)된 학생들만 가져오기
    @Query("SELECT e.student FROM Enrollment e WHERE e.room.id = :roomId")
    List<Student> findStudentsByRoomId(@Param("roomId") Long roomId);
}
