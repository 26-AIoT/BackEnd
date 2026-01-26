package com.AIoT.Back.domain;

import com.AIoT.Back.domain.constant.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status; // PRESENT, LATE, ABSENT

    private LocalDate attendanceDate; // 출석 기준 날짜 (2026-01-26)

    private LocalDateTime createdAt; // 실제 찍힌 시간

    public Attendance(Room room, Student student, AttendanceStatus status) {
        this.room = room;
        this.student = student;
        this.status = status;
        this.attendanceDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }
}