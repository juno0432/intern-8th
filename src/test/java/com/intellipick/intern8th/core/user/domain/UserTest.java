package com.intellipick.intern8th.core.user.domain;

import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;
import static org.junit.jupiter.api.Assertions.*;

import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("유저 생성 시 초기 상태는 삭제되지 않은 상태이다.")
    void createUser() {
        // given
        SignUpUserRequestDto requestDto = testSignUpUserRequestDto();

        // when
        User user = User.create(requestDto, "encryptPassword");

        // then
        assertFalse(user.isDeleted());
    }
}
