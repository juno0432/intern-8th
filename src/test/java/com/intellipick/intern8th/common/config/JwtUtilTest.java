package com.intellipick.intern8th.common.config;

import static com.intellipick.intern8th.common.constant.Const.BEARER_PREFIX;
import static com.intellipick.intern8th.common.constant.Const.USER_AUTHORITY_NAME;
import static com.intellipick.intern8th.common.constant.Const.USER_USERNAME;
import static com.intellipick.intern8th.data.UserTestData.testUserWithToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intellipick.intern8th.core.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private RedisTemplate<String, String> redisTemplate;
    private String secretAccessKey;
    private SecretKey accessKey;

    @BeforeEach
    void setUp() {
        redisTemplate = Mockito.mock(RedisTemplate.class);

        jwtUtil = new JwtUtil(redisTemplate);

        secretAccessKey = Base64.getEncoder().encodeToString("12345123451234512345123451234512345".getBytes());
        ReflectionTestUtils.setField(jwtUtil, "secretAccessKey", secretAccessKey);

        accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretAccessKey));
        jwtUtil.init();
    }

    @Nested
    @DisplayName("액세스 토큰 생성 시")
    class CreateAccessToken {

        @Test
        @DisplayName("정상적으로 액세스 토큰을 생성한다.")
        void createAccessTokenSuccessTest() {
            User user = testUserWithToken();

            String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getAuthorityName());

            assertNotNull(accessToken);
            assertTrue(accessToken.startsWith(BEARER_PREFIX));

            String token = jwtUtil.substringToken(accessToken);
            Claims claims = jwtUtil.getUserInfoFromToken(token);

            assertEquals(String.valueOf(user.getId()), claims.getSubject());
            assertEquals(user.getUsername(), claims.get(USER_USERNAME, String.class));
            assertEquals(user.getAuthorityName().getAuthorityName(), claims.get(USER_AUTHORITY_NAME, String.class));
        }
    }

    @Nested
    @DisplayName("토큰 검증 시")
    class ValidateToken {

        @Test
        @DisplayName("잘못된 토큰이면 에러를 발생시킨다.")
        void validationFailsDueToMalformed() {
            // Given
            String malformedToken = "Malformed";

            // When
            boolean result = jwtUtil.validateToken(malformedToken);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("토큰이 비어있으면 false를 반환한다.")
        void validationFailsDueToEmptyToken() {
            // Given
            String emptyToken = "";

            // When
            boolean result = jwtUtil.validateToken(emptyToken);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("토큰이 정상적으로 조회된다.")
        void validationSuccessTest() {
            // Given
            User user = testUserWithToken();

            // When
            String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getAuthorityName());
            String token = jwtUtil.substringToken(accessToken);

            //then
            assertTrue(jwtUtil.validateToken(token));
        }
    }

    @Nested
    @DisplayName("토큰 substring 시")
    class SubstringToken {

        @Test
        @DisplayName("토큰이 정확하지 않을때 예외를 발생시킨다.")
        void subStringTokenFailsDueToInvalidToken() {
            // Given
            String token = "invalidToken";

            // When & Then
            Exception exception = assertThrows(NullPointerException.class, () -> jwtUtil.substringToken(token));
            assertEquals("Not Found Token", exception.getMessage());
        }

        @Test
        @DisplayName("토큰이 null 일 때 예외를 발생시킨다.")
        void subStringTokenDueToEmptyToken() {
            // Given
            String token = null;

            // When & Then
            Exception exception = assertThrows(NullPointerException.class, () -> jwtUtil.substringToken(token));
            assertEquals("Not Found Token", exception.getMessage());
        }

        @Test
        @DisplayName("정상적으로 토큰을 substring 한다.")
        void subStringTokenSuccessTest() {
            // Given
            User user = testUserWithToken();

            String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getAuthorityName());
            String token = jwtUtil.substringToken(accessToken);
            String substringToken = accessToken.substring(7);

            // When & Then
            assertNotNull(token);
            assertEquals(substringToken, token);
        }
    }
}
