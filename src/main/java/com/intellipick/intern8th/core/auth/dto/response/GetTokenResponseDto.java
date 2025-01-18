package com.intellipick.intern8th.core.auth.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetTokenResponseDto {

    private final String token;
    private final String subStringToken;

    public static GetTokenResponseDto of(final String token, final String subStringToken) {
        return new GetTokenResponseDto(token, subStringToken);
    }
}
