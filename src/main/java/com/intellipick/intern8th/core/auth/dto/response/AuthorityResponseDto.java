package com.intellipick.intern8th.core.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(name = "AuthorityResponseDto - 권한 응답 DTO",
        description = "권한 응답 DTO")
public class AuthorityResponseDto {

    @Schema(title = "권한 이름", description = "권한 이름", example = "ROLE_USER")
    private final String authorityName;

    public static AuthorityResponseDto from(final String authorityName) {
        return new AuthorityResponseDto(authorityName);
    }
}
