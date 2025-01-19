package com.intellipick.intern8th.core.auth.service;

import static com.intellipick.intern8th.common.constant.Const.USER_AUTHORITY_NAME;
import static com.intellipick.intern8th.common.constant.Const.USER_USERNAME;
import static com.intellipick.intern8th.common.exception.ErrorCode.DUPLICATE_USER;
import static com.intellipick.intern8th.common.exception.ErrorCode.NOT_FOUND_USER;
import static com.intellipick.intern8th.common.exception.ErrorCode.PASSWORD_MISMATCH;
import static com.intellipick.intern8th.common.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;

import com.intellipick.intern8th.common.config.JwtUtil;
import com.intellipick.intern8th.common.exception.ApplicationException;
import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetTokenResponseDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.domain.UserRole;
import com.intellipick.intern8th.core.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public GetUserResponseDto signUp(final SignUpUserRequestDto signUpUserRequestDto) {
        boolean doesUserExist = userRepository.existsByUsername(signUpUserRequestDto.getUsername());

        if (doesUserExist) {
            throw new ApplicationException(DUPLICATE_USER);
        }

        String encryptPassword = passwordEncoder.encode(signUpUserRequestDto.getPassword());
        User user = User.create(signUpUserRequestDto, encryptPassword);
        userRepository.save(user);

        return GetUserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public GetTokenResponseDto signIn(final SignInUserRequestDto signInUserRequestDto) {
        User user = userRepository.findByUsernameOrThrow(signInUserRequestDto.getUsername());

        if (!passwordEncoder.matches(signInUserRequestDto.getPassword(), user.getPassword())) {
            throw new ApplicationException(PASSWORD_MISMATCH);
        }

        if (user.isDeleted()) {
            throw new ApplicationException(NOT_FOUND_USER);
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getAuthorityName());
        jwtUtil.createRefreshToken(user.getId(), user.getUsername(), user.getAuthorityName());

        return GetTokenResponseDto.of(accessToken, jwtUtil.substringToken(accessToken));
    }

    //사용하지는 않지만, 리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급하는 메서드입니다.
    @Transactional(readOnly = true)
    public String getAccessToken(final Long userId) {

        String redisKey = "user:refresh:id:" + userId;

        String redisToken = redisTemplate.opsForValue().get(redisKey);
        redisToken = jwtUtil.substringToken(redisToken);

        Claims claims = jwtUtil.getUserInfoFromToken(redisToken);

        if (redisToken == null) {
            throw new ApplicationException(REFRESH_TOKEN_NOT_FOUND);
        }

        //토큰에서 새로운 acceesToken을 생성하기위해 email과 role을 가져옵니다.
        String username = claims.get(USER_USERNAME, String.class);
        String authority = claims.get(USER_AUTHORITY_NAME, String.class);

        return jwtUtil.createAccessToken(userId, username, UserRole.valueOf(authority));
    }
}
