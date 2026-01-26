package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Enrollment;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 이미 이 방에 등록된 학생인지 확인
    boolean existsByRoomAndStudent(Room room, Student student);
    Optional<Enrollment> findByRoomAndStudent(Room room, Student student);
}
