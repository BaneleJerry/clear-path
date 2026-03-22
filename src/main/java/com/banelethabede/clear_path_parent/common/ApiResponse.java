package com.banelethabede.clear_path_parent.common;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
public class ApiResponse<T> {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private T data;
}