package com.intellipick.intern8th.data;

import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;

import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.user.domain.User;

public class UserTestData {

    public static User testUser() {
        SignUpUserRequestDto userRequestDto = testSignUpUserRequestDto();
        return User.create(userRequestDto, "password");
    }
}
