package com.AIoT.Back.utils;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).data(data).build();
    }

    public static ApiResponse<Void> ok() {
        return ApiResponse.<Void>builder().success(true).build();
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .error(new ApiError(code, message))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
    }
}
