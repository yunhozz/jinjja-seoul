package com.jinjjaseoul.domain.user.model.repository;

import com.jinjjaseoul.domain.user.model.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    Optional<UserRefreshToken> findByUserId(Long userId);
    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);
}