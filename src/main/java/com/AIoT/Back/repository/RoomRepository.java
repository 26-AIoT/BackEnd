package com.AIoT.Back.repository;

import com.AIoT.Back.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    // 선생님 ID로 방 목록 찾기 (최신순 정렬: Id Desc)
    List<Room> findAllByTeacherIdOrderByIdDesc(Long teacherId);
    Optional<Room> findByRoomCode(String roomCode);
}
