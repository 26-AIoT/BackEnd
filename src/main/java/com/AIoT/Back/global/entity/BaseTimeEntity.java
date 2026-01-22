package com.AIoT.Back.global.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // "나는 테이블이 아니라, 자식들에게 컬럼만 물려주는 부모야"
@EntityListeners(AuditingEntityListener.class) // "JPA야, 나를 감시하다가 시간 변화가 생기면 알려줘"
@Getter
public abstract class BaseTimeEntity {

    @CreatedDate // 생성될 때 자동으로 시간 저장
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정될 때 자동으로 시간 저장
    private LocalDateTime modifiedAt;
}