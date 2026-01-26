package com.AIoT.Back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}