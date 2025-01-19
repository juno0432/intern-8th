package com.intellipick.intern8th.core.auth.service;

import static com.intellipick.intern8th.common.constant.Const.USER_AUTHORITY_NAME;
import static com.intellipick.intern8th.common.constant.Const.USER_USERNAME;
import static com.intellipick.intern8th.data.AuthTestData.testSignInUserRequestDto;
import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;
import static com.intellipick.intern8th.data.UserTestData.testUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.intellipick.intern8th.common.config.JwtUtil;
import com.intellipick.intern8th.common.exception.ApplicationException;
import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;


    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

        @Test
        @DisplayName("username이 중복되면 회원가입이 실패한다.")
        void signUpFailDuplicateUser() {
            // given
            SignUpUserRequestDto signUpUserRequestDto = testSignUpUserRequestDto();

            given(userRepository.existsByUsername(signUpUserRequestDto.getUsername())).willReturn(true);

            // when - then
            assertThrows(ApplicationException.class, () -> authService.signUp(signUpUserRequestDto));
        }

        @Test
        @DisplayName("회원가입이 성공한다.")
        void signUpSuccess() {
            // given
            SignUpUserRequestDto signUpUserRequestDto = testSignUpUserRequestDto();

            String password = passwordEncoder.encode(signUpUserRequestDto.getPassword());
            User user = testUser();

            given(userRepository.existsByUsername(signUpUserRequestDto.getUsername())).willReturn(false);
            given(passwordEncoder.encode(signUpUserRequestDto.getPassword())).willReturn(password);
            given(userRepository.save(any(User.class))).willReturn(user);

            // when
            GetUserResponseDto getUserResponseDto = authService.signUp(signUpUserRequestDto);

            // then
            assertThat(getUserResponseDto).isNotNull();
            assertThat(getUserResponseDto.getUsername()).isEqualTo(user.getUsername());
            assertThat(getUserResponseDto.getNickname()).isEqualTo(user.getNickname());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class SignInTest {

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인이 401에러가 발생한다.")
        void signInFailPasswordMismatch() {
            // given
            SignInUserRequestDto signInUserRequestDto = testSignInUserRequestDto();
            User user = testUser();

            given(userRepository.findByUsernameOrThrow(signInUserRequestDto.getUsername())).willReturn(user);
            given(passwordEncoder.matches(signInUserRequestDto.getPassword(), user.getPassword())).willReturn(false);

            // when & then
            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signIn(signInUserRequestDto));
            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("삭제된 사용자로 로그인 시도 시 404에러가 발생한다.")
        void signInFailDeletedUser() {
            // given
            SignInUserRequestDto signInUserRequestDto = testSignInUserRequestDto();
            User user = testUser();
            ReflectionTestUtils.setField(user, "isDeleted", true);

            given(userRepository.findByUsernameOrThrow(signInUserRequestDto.getUsername())).willReturn(user);
            given(passwordEncoder.matches(signInUserRequestDto.getPassword(), user.getPassword())).willReturn(true);

            // when - then
            ApplicationException exception = assertThrows(ApplicationException.class,
                    () -> authService.signIn(signInUserRequestDto));
            assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("로그인 성공")
        void signInSuccess() {
            // given
            SignInUserRequestDto signInUserRequestDto = testSignInUserRequestDto();
            User user = testUser();

            given(userRepository.findByUsernameOrThrow(signInUserRequestDto.getUsername())).willReturn(user);
            given(passwordEncoder.matches(signInUserRequestDto.getPassword(), user.getPassword())).willReturn(true);

            // when - then
            assertDoesNotThrow(() -> authService.signIn(signInUserRequestDto));
        }
    }

    @Nested
    @DisplayName("액세스 토큰 재발급 테스트")
    class GetAccessTokenTest {

        @Test
        @DisplayName("리프레시 토큰이 저장되어 있지않으면 액세스 토큰 재발급이 실패한다.")
        void getAccessTokenFailRefreshTokenNotFound() {
            // given
            Long userId = 1L;
            String redisKey = "user:refresh:id:" + userId;

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(redisKey)).willReturn(null);

            // when - then
            assertThrows(ApplicationException.class, () -> authService.getAccessToken(userId));
        }

        @Test
        @DisplayName("액세스 토큰 재발급 성공")
        void getAccessTokenSuccess() {
            // given
            Long userId = 1L;
            String redisKey = "user:refresh:id:" + userId;
            String redisToken = "Bearer token";
            String token = "token";

            Claims claims = Mockito.mock(Claims.class);
            User user = testUser();

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get(redisKey)).willReturn(redisToken);
            given(jwtUtil.substringToken(redisToken)).willReturn(token);
            given(jwtUtil.getUserInfoFromToken(token)).willReturn(claims);
            given(jwtUtil.createAccessToken(userId, user.getUsername(), user.getAuthorityName())).willReturn(token);
            given(claims.get(USER_USERNAME, String.class)).willReturn(user.getUsername());
            given(claims.get(USER_AUTHORITY_NAME, String.class)).willReturn(user.getAuthorityName().getAuthorityName());

            String expectedToken = authService.getAccessToken(userId);

            // when - then
            assertThat(expectedToken).isEqualTo(token);
        }
    }
}
