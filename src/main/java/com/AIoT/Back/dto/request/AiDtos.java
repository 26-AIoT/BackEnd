package com.AIoT.Back.dto.request;

import lombok.*;

import java.util.List;

//AI가 보낼 데이터를 받을 그릇
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDtos {
    // AI가 보낸 512차원 벡터 데이터
    private List<Double> vector;
}