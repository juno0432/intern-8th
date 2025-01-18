package com.intellipick.intern8th.common.config;

import static com.intellipick.intern8th.common.constant.Const.BEARER_PREFIX;
import static com.intellipick.intern8th.common.constant.Const.TOKEN_ACCESS_TIME;
import static com.intellipick.intern8th.common.constant.Const.TOKEN_REFERSH_TIME;
import static com.intellipick.intern8th.common.constant.Const.USER_AUTHORITY_NAME;
import static com.intellipick.intern8th.common.constant.Const.USER_USERNAME;

import com.intellipick.intern8th.core.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret.key}")
    private String secretAccessKey;
    private SecretKey accessKey;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretAccessKey));
    }

    // 액세스 토큰 생성
    public String createAccessToken(Long userId, String username, UserRole authorityName) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .subject(String.valueOf(userId))
                        .claim(USER_USERNAME, username)
                        .claim(USER_AUTHORITY_NAME, authorityName)
                        .expiration(new Date(date.getTime() + TOKEN_ACCESS_TIME))
                        .issuedAt(date)
                        .signWith(accessKey)
                        .compact();
    }

    //리프레시 토큰 생성
    public String createRefreshToken(Long userId, String username, UserRole authorityName) {
        Date date = new Date();

        String refreshToken = BEARER_PREFIX +
                Jwts.builder()
                        .subject(String.valueOf(userId))
                        .claim(USER_USERNAME, username)
                        .claim(USER_AUTHORITY_NAME, authorityName)
                        .expiration(new Date(date.getTime() + TOKEN_REFERSH_TIME))
                        .issuedAt(date)
                        .signWith(accessKey)
                        .compact();

        String key = "user:id:" + userId;
        redisTemplate.opsForValue().set(key, refreshToken, TOKEN_REFERSH_TIME, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(accessKey).build().parseSignedClaims(token).getPayload();
            return true; // 유효한 토큰
        }
        catch (SecurityException | MalformedJwtException e) {
            logger.error("잘못된 JWT 서명입니다.");
        }
        catch (ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰입니다.");
        }
        catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT 토큰입니다.");
        }
        catch (IllegalArgumentException e) {
            logger.error("JWT 토큰이 잘못되었습니다.");
        }
        return false; // 유효하지 않은 토큰
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            // "Bearer "이 공백을 포함하여 7자를 자른다.
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
