package com.intellipick.intern8th.common.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    NOT_FOUND_TOKEN(UNAUTHORIZED, "토큰을 찾을 수 없습니다."),
    PASSWORD_MISMATCH(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USER(CONFLICT, "이미 존재하는 사용자 입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
