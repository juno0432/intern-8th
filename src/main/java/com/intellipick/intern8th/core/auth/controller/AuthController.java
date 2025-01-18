package com.intellipick.intern8th.core.auth.controller;

import com.intellipick.intern8th.common.exception.ErrorResponseDto;
import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetTokenResponseDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
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
@Tag(name = "AuthController - 인증 컨트롤러",
        description = "인증과 관련된 API를 제공하는 컨트롤러입니다.")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 로직입니다
     *
     * @param signUpUserRequestDto username, password, nickname을 입력받습니다.
     * @return 상태코드 201 created를 반환합니다.
     */
    @PostMapping("/auth/signup")
    @Operation(
            summary = "회원가입 API",
            description = "회원가입을 위한 API입니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "회원가입 성공",
                            content = @Content(
                                    schema = @Schema(implementation = GetUserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "회원가입 실패 - 이미 존재하는 사용자 입니다.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n  \"statusCode\": 409,\n  \"message\": \"이미 존재하는 사용자 입니다.\"\n}"
                                    ),
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<GetUserResponseDto> signUp(
            @Valid @RequestBody final SignUpUserRequestDto signUpUserRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signUp(signUpUserRequestDto));
    }

    /**
     * 로그인 로직입니다
     *
     * @param signInUserRequestDto username, password를 입력받습니다.
     * @return 200 OK와 토큰값을 반환합니다.
     */
    @PostMapping("/auth/sign")
    @Operation(
            summary = "로그인 API",
            description = "로그인을 위한 API입니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(
                                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1...")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "로그인 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n  \"statusCode\": 401,\n  \"message\": \"비밀번호가 일치하지 않습니다.\"\n}"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Map<String, String>> signIn(
            @Valid @RequestBody final SignInUserRequestDto signInUserRequestDto
    ) {
        GetTokenResponseDto tokenDto = authService.signIn(signInUserRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("token", tokenDto.getSubStringToken());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Authorization", tokenDto.getToken())
                .body(response);
    }
}
