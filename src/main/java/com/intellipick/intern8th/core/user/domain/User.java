package com.intellipick.intern8th.core.user.domain;

import com.intellipick.intern8th.common.entity.Timestamped;
import com.intellipick.intern8th.core.auth.dto.request.SignUpUserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String username;

    @Column
    @NotNull
    private String password;

    @Column
    @NotNull
    private String nickname;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole authorityName;

    @Column
    private boolean isDeleted;

    private User(final String username, final String password, final String nickname, final UserRole authorityName) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.authorityName = authorityName;
        this.isDeleted = false;
    }

    public static User create(final SignUpUserRequestDto signUpUserRequestDto, final String encryptPassword) {
        return new User(
                signUpUserRequestDto.getUsername(),
                encryptPassword,
                signUpUserRequestDto.getNickname(),
                UserRole.ROLE_USER
        );
    }
}
