package com.intellipick.intern8th.core.auth.dto.response;

import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(name = "GetUserResponseDto - 사용자 조회 응답 DTO",
        description = "사용자 조회 응답 DTO")
public class GetUserResponseDto {

    @Schema(title = "이름", description = "이름", example = "james")
    private final String username;
    @Schema(title = "닉네임", description = "닉네임", example = "jame")
    private final String nickname;
    private final List<AuthorityResponseDto> authorities;

    public static GetUserResponseDto from(final User user) {
        UserRole userRole = user.getAuthorityName();
        AuthorityResponseDto authorityResponse = AuthorityResponseDto.from(userRole.getAuthorityName());

        List<AuthorityResponseDto> authorityResponses = List.of(authorityResponse);

        return new GetUserResponseDto(user.getUsername(), user.getNickname(), authorityResponses);
    }
}
