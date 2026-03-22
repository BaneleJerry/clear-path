package com.banelethabede.clear_path_parent.common;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ApiResponseUtil {

    public static <T> ApiResponse<T> success(String message, String path, T data) {
        return ApiResponse.<T>builder()
                .timestamp(OffsetDateTime.now())
                .status(200)
                .error(null)
                .message(message)
                .path(path)
                .data(data)
                .build();
    }
}