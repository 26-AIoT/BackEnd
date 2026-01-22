package com.AIoT.Back.teacher.repository;

import com.AIoT.Back.teacher.entity.TeacherUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<TeacherUser, Long> {
    Optional<TeacherUser> findByEmail(String email);
}
