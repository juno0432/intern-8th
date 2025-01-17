package com.intellipick.intern8th.core.user.domain;

import static com.intellipick.intern8th.common.exception.ErrorCode.NOT_FOUND_TOKEN;

import com.intellipick.intern8th.common.exception.ApplicationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ROLE_USER(Authority.USER);

    private final String authorityName;

    public static UserRole from(final String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new ApplicationException(NOT_FOUND_TOKEN));
    }

    public static class Authority {

        public static final String USER = "ROLE_USER";
    }
}
