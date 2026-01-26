package com.AIoT.Back.dto.request;

import lombok.Data;

import java.util.List;

//AI가 보낼 데이터를 받을 그릇
@Data
public class AiDtos {
    private String roomCode;
    private List<Double> vector; // 512
    private Double score; // 집중도
}
