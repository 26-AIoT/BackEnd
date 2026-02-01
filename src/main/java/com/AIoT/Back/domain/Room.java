package com.AIoT.Back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false, unique = true)
    private String roomCode; // 학생 접속용 난수 코드

    private LocalDateTime startedAt;

    // 방 주인 (선생님)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // 이 방을 듣는 학생들 (중간 테이블 Enrollment 매핑)
    @OneToMany(mappedBy = "room")
    private List<Enrollment> enrollments = new ArrayList<>();

    public Room(String roomName, String roomCode, Teacher teacher) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.teacher = teacher;
    }

    // ★ [추가] 수업 시작/종료 메서드 (비즈니스 로직)
    public void startClass() {
        this.startedAt = LocalDateTime.now();
    }

    public void endClass() {
        this.startedAt = null;
    }

    // ★ [추가] 현재 수업 중인지 확인
    public boolean isClassRunning() {
        return this.startedAt != null;
    }
}