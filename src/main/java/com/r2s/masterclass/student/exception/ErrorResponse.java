package com.r2s.masterclass.student.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Định dạng lỗi chuẩn trả về client.
 * Trường errors chỉ xuất hiện với lỗi validation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxx")
        OffsetDateTime timestamp,
        int status,
        String message,
        Map<String, String> errors
) {

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(OffsetDateTime.now(ZoneOffset.UTC), status, message, null);
    }

    public static ErrorResponse of(int status, String message, Map<String, String> errors) {
        return new ErrorResponse(OffsetDateTime.now(ZoneOffset.UTC), status, message, errors);
    }
}
