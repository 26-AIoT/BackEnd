package com.AIoT.Back.teacher.service;

import com.AIoT.Back.teacher.dto.TeacherDtos;
import com.AIoT.Back.teacher.entity.TeacherUser;
import com.AIoT.Back.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherUser join(TeacherDtos.RequestJoin req) {

        teacherRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        });

        TeacherUser teacherUser = TeacherUser.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build();

        teacherRepository.save(teacherUser).getId();
        return teacherUser;
    }

    public TeacherUser login(TeacherDtos.RequestLogin req) {
        TeacherUser teacher = teacherRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), teacher.getPassword())) {
            throw new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다.");
        }

        return teacher;
    }
}
