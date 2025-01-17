package com.intellipick.intern8th.core.auth.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorityResponseDto {

    private final String authorityName;

    public static AuthorityResponseDto from(final String authorityName) {
        return new AuthorityResponseDto(authorityName);
    }
}
