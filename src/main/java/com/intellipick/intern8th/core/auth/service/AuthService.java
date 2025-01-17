package com.intellipick.intern8th.core.auth.service;

import static com.intellipick.intern8th.common.exception.ErrorCode.DUPLICATE_USER;

import com.intellipick.intern8th.common.config.PasswordEncoder;
import com.intellipick.intern8th.common.exception.ApplicationException;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GetUserResponseDto signUp(SignUpUserRequestDto signUpUserRequestDto) {
        boolean isExistUser = userRepository.existsByUsername(signUpUserRequestDto.getUsername());

        if (isExistUser) {
            throw new ApplicationException(DUPLICATE_USER);
        }

        String encryptPassword = passwordEncoder.encode(signUpUserRequestDto.getPassword());
        User user = User.create(signUpUserRequestDto, encryptPassword);
        userRepository.save(user);

        return GetUserResponseDto.from(user);
    }
}
