package com.intellipick.intern8th.core.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(name = "SignUpUserRequestDto - 사용자 회원가입 요청 DTO",
        description = "사용자 회원가입 요청 DTO")
public class SignUpUserRequestDto {

    @Schema(title = "이름", description = "이름", example = "james")
    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @Schema(title = "비밀번호", description = "비밀번호", example = "12345")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(title = "닉네임", description = "닉네임", example = "jame")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}
