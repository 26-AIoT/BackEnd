package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Enrollment;
import com.AIoT.Back.domain.Room;
import com.AIoT.Back.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // 이미 이 방에 등록된 학생인지 확인 (중복 수강신청 방지)
    boolean existsByRoomAndStudent(Room room, Student student);

    // 특정 학생이 특정 방에 등록된 정보 가져오기 (수강 취소 등에 사용)
    Optional<Enrollment> findByRoomAndStudent(Room room, Student student);

    // 이 방의 모든 수강생 목록 (Enrollment 객체로 반환)
    List<Enrollment> findAllByRoom(Room room);

    // 특정 학생이 듣고 있는 모든 수업 목록
    List<Enrollment> findAllByStudent(Student student);

    // 방에 몇 명이 있는지 숫자 세기
    int countByRoom(Room room);
}
