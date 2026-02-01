package com.AIoT.Back.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSessionDtos {
    private Long studentId;
    private boolean verificationRequested; // 검사 요청 여부
}
