package com.AIoT.Back.service;

import com.AIoT.Back.domain.Student;
import com.AIoT.Back.dto.request.StudentDtos;
import com.AIoT.Back.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 테스트용 (실제에서는 안씀!)

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public Long join(StudentDtos.JoinRequest request) {
        // 이미 있는 학번인지 체크 (생략 가능하지만 안전하게)
        if (studentRepository.findByStudentNumber(request.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 학번입니다.");
        }

        Student student = new Student(request.getName(), request.getStudentNumber(), request.getFaceVector());
        studentRepository.save(student);
        return student.getId();
    }
}
