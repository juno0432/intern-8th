package com.intellipick.intern8th.core.user.repository;

import com.intellipick.intern8th.core.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
}
