package com.intellipick.intern8th.core.auth.controller;

import static com.intellipick.intern8th.common.exception.ErrorCode.DUPLICATE_USER;
import static com.intellipick.intern8th.common.exception.ErrorCode.NOT_FOUND_USER;
import static com.intellipick.intern8th.common.exception.ErrorCode.PASSWORD_MISMATCH;
import static com.intellipick.intern8th.data.AuthTestData.testGetTokenResponseDto;
import static com.intellipick.intern8th.data.AuthTestData.testGetUserResponseDto;
import static com.intellipick.intern8th.data.AuthTestData.testSignInUserRequestDto;
import static com.intellipick.intern8th.data.AuthTestData.testSignUpUserRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellipick.intern8th.common.config.JwtUtil;
import com.intellipick.intern8th.common.exception.ApplicationException;
import com.intellipick.intern8th.core.auth.dto.request.SignInUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import com.intellipick.intern8th.core.auth.dto.response.GetUserResponseDto;
import com.intellipick.intern8th.core.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("회원가입 시")
    class signUp {

        @Test
        @DisplayName("이미 존재하는 username일 경우 회원가입이 실패된다.")
        void signUpFailTest() throws Exception {
            //given
            SignUpUserRequestDto requestDto = testSignUpUserRequestDto();

            //when
            given(authService.signUp(any(SignUpUserRequestDto.class))).willThrow(
                    new ApplicationException(DUPLICATE_USER));

            //then
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf().asHeader()))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("회원가입이 성공한다.")
        void signUpSuccessTest() throws Exception {
            //given
            SignUpUserRequestDto requestDto = testSignUpUserRequestDto();
            GetUserResponseDto responseDto = testGetUserResponseDto();

            //when
            given(authService.signUp(any(SignUpUserRequestDto.class))).willReturn(responseDto);

            //then
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf().asHeader()))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("로그인 시")
    class signIn {

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 로그인이 실패한다.")
        void loginFailsDueToIncorrectPassword() throws Exception {
            // given
            SignInUserRequestDto requestDto = testSignInUserRequestDto();

            // when
            given(authService.signIn(any(SignInUserRequestDto.class))).willThrow(
                    new ApplicationException(PASSWORD_MISMATCH));

            //when & then
            mockMvc.perform(post("/api/v1/auth/sign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf().asHeader()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("사용자를 찾을 수 없으면 로그인이 실패한다.")
        void signInFailsWhenUserNotFound() throws Exception {
            // given
            SignInUserRequestDto requestDto = testSignInUserRequestDto();

            // when
            given(authService.signIn(any(SignInUserRequestDto.class))).willThrow(
                    new ApplicationException(NOT_FOUND_USER));

            //when & then
            mockMvc.perform(post("/api/v1/auth/sign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf().asHeader()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("로그인이 성공하며 token을 반환한다.")
        void signInSuccessTest() throws Exception {
            // given
            SignInUserRequestDto requestDto = testSignInUserRequestDto();

            // when
            given(authService.signIn(any(SignInUserRequestDto.class))).willReturn(testGetTokenResponseDto());

            //when & then
            mockMvc.perform(post("/api/v1/auth/sign")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto))
                            .with(csrf().asHeader()))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.AUTHORIZATION, testGetTokenResponseDto().getToken()));
        }
    }
}
