package com.AIoT.Back.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {

    PRESENT("출석"),
    LATE("지각"),
    ABSENT("결석");

    private final String description; // 한글 설명이 필요할 경우를 위해 추가
}