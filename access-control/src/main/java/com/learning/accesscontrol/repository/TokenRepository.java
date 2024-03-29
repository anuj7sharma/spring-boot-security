package com.learning.accesscontrol.repository;

import com.learning.accesscontrol.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
Optional<TokenEntity> findByToken(String token);

    Optional<TokenEntity> findByTokenTypeAndUserId(String tokenType, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE TokenEntity t " +
            "SET t.confirmedAt = ?2 " +
            "WHERE t.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);

    @Transactional
    @Modifying
    @Query("UPDATE TokenEntity t " +
            "SET t.expiresAt = ?2 " +
            "WHERE t.token = ?1")
    int updateExpiresAt(String token,
                        LocalDateTime expiresAt);
}
