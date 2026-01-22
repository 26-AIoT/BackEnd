package com.AIoT.Back.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Room {

    @Id
    private String roomId;   // UUID로 생성 예정

    private String roomName;
    private Long hostId;     // 선생님 ID
    private boolean active;  // 방 활성화 여부
}