package com.intellipick.intern8th.data;

import static com.intellipick.intern8th.data.UserTestData.testUser;

import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetTokenResponseDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;

public class AuthTestData {

    public static SignUpUserRequestDto testSignUpUserRequestDto() {
        return new SignUpUserRequestDto("test", "1234", "test");
    }

    public static SignInUserRequestDto testSignInUserRequestDto() {
        return new SignInUserRequestDto("test", "1234");
    }

    public static GetUserResponseDto testGetUserResponseDto() {
        return GetUserResponseDto.from(testUser());
    }

    public static GetTokenResponseDto testGetTokenResponseDto() {
        return GetTokenResponseDto.of("accessToken", "refreshToken");
    }
}
