package com.intellipick.intern8th.core.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(name = "GetTokenResponseDto - 토큰 응답 DTO", description = "토큰 응답 DTO")
public class GetTokenResponseDto {

    @Schema(title = "Bearer 토큰", description = "Bearer 토큰", example = "Bearer eyJhbGciOi~")
    private final String token;
    @Schema(title = "SubString 토큰", description = "SubString 토큰", example = "eyJhbGciOi~")
    private final String subStringToken;

    public static GetTokenResponseDto of(final String token, final String subStringToken) {
        return new GetTokenResponseDto(token, subStringToken);
    }
}
