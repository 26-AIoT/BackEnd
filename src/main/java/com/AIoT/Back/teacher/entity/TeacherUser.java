package com.AIoT.Back.teacher.entity;

import com.AIoT.Back.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherUser extends BaseTimeEntity {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

}
