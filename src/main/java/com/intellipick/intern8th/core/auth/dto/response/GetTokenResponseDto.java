package com.intellipick.intern8th.core.auth.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetTokenResponseDto {

    private final String token;

    public static GetTokenResponseDto from(final String token) {
        return new GetTokenResponseDto(token);
    }
}
