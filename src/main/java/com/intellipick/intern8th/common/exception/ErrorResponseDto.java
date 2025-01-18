package com.intellipick.intern8th.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
@Schema(name = "ErrorResponseDto - 에러 응답 DTO", description = "에러 응답 DTO")
public class ErrorResponseDto {

    @Schema(description = "상태 코드", example = "400")
    private final int statusCode;
    @Schema(description = "메시지", example = "잘못된 요청입니다.")
    private final String message;
    @Schema(hidden = true)
    private final Map<String, String> validations = new HashMap<>();

    private ErrorResponseDto(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static ErrorResponseDto of(final int statusCode, final String message) {
        return new ErrorResponseDto(statusCode, message);
    }

    public void addValidation(final String fieldName, final String message) {
        validations.put(fieldName, message);
    }
}
