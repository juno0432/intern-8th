package com.intellipick.intern8th.core.auth.controller;

import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 로직입니다
     *
     * @param signUpUserRequestDto username, password, nickname을 입력받습니다.
     * @return 상태코드 201 created를 반환합니다.
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<GetUserResponseDto> signUp(
            @Valid @RequestBody SignUpUserRequestDto signUpUserRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signUp(signUpUserRequestDto));
    }
}
