package com.intellipick.intern8th.core.auth.service;

import static com.intellipick.intern8th.common.exception.ErrorCode.DUPLICATE_USER;
import static com.intellipick.intern8th.common.exception.ErrorCode.PASSWORD_MISMATCH;

import com.intellipick.intern8th.common.config.JwtUtil;
import com.intellipick.intern8th.common.exception.ApplicationException;
import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetTokenResponseDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername(), user.getAuthorityName());
        jwtUtil.createRefreshToken(user.getId(), user.getUsername(), user.getAuthorityName());

        return GetTokenResponseDto.of(accessToken, jwtUtil.substringToken(accessToken));
    }
}
