package com.AIoT.Back.student.entity;

import com.AIoT.Back.rooms.Room;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Student")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUser {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String sessionId; // 소켓 연결 끊김 감지용

    @Column(nullable = false)
    private String isAttended; // 출석인정여부
}