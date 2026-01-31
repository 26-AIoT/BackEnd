package com.AIoT.Back.domain;

import com.AIoT.Back.domain.converter.FaceVectorConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String studentNumber; // 학번

    // ★ 핵심: 512차원 벡터를 JSON String으로 변환하여 저장
    @Convert(converter = FaceVectorConverter.class)
    @Column(columnDefinition = "json") // MySQL JSON 타입 사용
    private List<Double> faceVector;

    // 학생이 듣는 수업 목록
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(String name, String studentNumber, List<Double> faceVector) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.faceVector = faceVector;
    }
}