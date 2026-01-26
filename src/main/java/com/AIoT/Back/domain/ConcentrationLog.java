package com.AIoT.Back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = @Index(name = "idx_concentration_room_student", columnList = "room_id, student_id")) // 조회 성능 최적화 인덱스
public class ConcentrationLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private Double score; // 집중도 점수

    private Integer eyeStatus; // 0: 뜸, 1: 감음

    private LocalDateTime timestamp;

    public ConcentrationLog(Room room, Student student, Double score, Integer eyeStatus) {
        this.room = room;
        this.student = student;
        this.score = score;
        this.eyeStatus = eyeStatus;
        this.timestamp = LocalDateTime.now();
    }
}