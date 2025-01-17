package com.intellipick.intern8th.core.auth.dto.response;

import com.intellipick.intern8th.core.user.domain.User;
import com.intellipick.intern8th.core.user.domain.UserRole;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetUserResponseDto {

    private final String username;
    private final String nickname;
    private final List<AuthorityResponseDto> authorities;

    public static GetUserResponseDto from(final User user) {
        UserRole userRole = user.getAuthorityName();
        AuthorityResponseDto authorityResponse = AuthorityResponseDto.from(userRole.getAuthorityName());

        List<AuthorityResponseDto> authorityResponses = List.of(authorityResponse);

        return new GetUserResponseDto(user.getUsername(), user.getNickname(), authorityResponses);
    }
}
