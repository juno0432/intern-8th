package com.intellipick.intern8th.common.annotation;

import com.intellipick.intern8th.core.user.domain.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class AuthUser {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(final Long userId, final String username, final UserRole role) {
        this.userId = userId;
        this.username = username;
        this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
    }
}
