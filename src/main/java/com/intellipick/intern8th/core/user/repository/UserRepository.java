package com.intellipick.intern8th.core.user.repository;

import com.intellipick.intern8th.core.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(final String username);

    Optional<User> findByUsername(final String username);

    default User findByUsernameOrThrow(final String username) {
        return findByUsername(username).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }
}
