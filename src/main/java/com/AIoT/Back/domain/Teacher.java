package com.AIoT.Back.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Teacher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email; // 로그인 ID 역할

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // 선생님이 만든 수업 목록
    @OneToMany(mappedBy = "teacher")
    private List<Room> rooms = new ArrayList<>();

    // 생성자
    public Teacher(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}