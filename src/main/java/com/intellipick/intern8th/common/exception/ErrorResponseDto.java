package com.intellipick.intern8th.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponseDto {

    private final int statusCode;
    private final String message;
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
