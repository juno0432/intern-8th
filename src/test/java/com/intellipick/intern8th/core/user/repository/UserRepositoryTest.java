package com.intellipick.intern8th.core.user.repository;

import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intellipick.intern8th.core.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("findByUsernameOrThrow 메소드")
    class Describe_findByUsernameOrThrow {

        @Test
        @DisplayName("해당 유저를 찾을 수 없을 때 IllegalArgumentException을 던진다.")
        void it_throws_IllegalArgumentException_when_user_not_found() {
            // when & then
            assertThatThrownBy(() -> userRepository.findByUsernameOrThrow("notExist"))
                    .hasMessage("해당 유저를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("해당 유저를 찾을 수 있을 때 User를 반환한다.")
        void it_returns_User_when_user_found() {
            // given
            User user = User.create(testSignUpUserRequestDto(), "test");
            userRepository.save(user);

            // when
            User foundUser = userRepository.findByUsernameOrThrow("test");

            // then
            assertNotNull(foundUser);
            assertEquals("test", foundUser.getUsername());
        }
    }

}
