package com.intellipick.intern8th.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        int statusCode = BAD_REQUEST.value();
        String message = "잘못된 요청입니다.";
        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(statusCode, message);

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            log.error("{} - {} : {}", exception.getClass().getSimpleName(), fieldError.getField(),
                    fieldError.getDefaultMessage());
            errorResponseDto.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResponseDto;
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationException(ApplicationException exception) {
        log.error("{} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(exception.getStatusCode(), exception.getMessage());

        return ResponseEntity.status(errorResponseDto.getStatusCode())
                .body(errorResponseDto);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {
        String exceptionMessage = String.format("%s는 필수 입력 항목입니다.", exception.getParameterName());
        log.error("{} - {}", exception.getClass().getSimpleName(), exceptionMessage);

        ErrorResponseDto errorResponseDto = ErrorResponseDto.of(BAD_REQUEST.value(), exceptionMessage);

        return ResponseEntity.status(errorResponseDto.getStatusCode())
                .body(errorResponseDto);
    }
}
