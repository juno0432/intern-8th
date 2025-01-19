package com.intellipick.intern8th.data;

import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;

import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.user.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTestData {

    public static User testUser() {
        SignUpUserRequestDto userRequestDto = testSignUpUserRequestDto();
        return User.create(userRequestDto, "password");
    }

    public static User testUserWithToken() {
        User user = testUser();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    public static User testDeletedUser() {
        User user = testUser();
        ReflectionTestUtils.setField(user, "isDeleted", true);
        return user;
    }
}
