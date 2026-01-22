package com.AIoT.Back.service;

import com.AIoT.Back.dto.request.LoginReq;
import com.AIoT.Back.domain.Teacher;
import com.AIoT.Back.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public Teacher join(LoginReq.RequestJoin req) {

        teacherRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        });

        Teacher teacher = Teacher.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build();

        teacherRepository.save(teacher).getId();
        return teacher;
    }

    public Teacher login(LoginReq.RequestLogin req) {
        Teacher teacher = teacherRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), teacher.getPassword())) {
            throw new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다.");
        }

        return teacher;
    }
}
