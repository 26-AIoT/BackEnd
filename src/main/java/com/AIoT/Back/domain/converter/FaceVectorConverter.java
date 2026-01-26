package com.AIoT.Back.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Converter
public class FaceVectorConverter implements AttributeConverter<List<Double>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Double> attribute) {
        // 자바 List -> JSON String (DB 저장용)
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Vector converting error", e);
        }
    }

    @Override
    public List<Double> convertToEntityAttribute(String dbData) {
        // JSON String -> 자바 List (사용용)
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Vector converting error", e);
        }
    }
}