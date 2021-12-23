package com.learning.accesscontrol.service;

import com.learning.accesscontrol.entity.TokenEntity;
import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.repository.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;

    @Override
    public void saveConfirmationToken(TokenEntity tokenEntity) {
        tokenRepository.save(tokenEntity);
    }

    @Override
    public Optional<TokenEntity> getConfirmationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public Optional<TokenEntity> getTokenByTypeAndUserId(TokenType tokenType, UserEntity userEntity) {
        return tokenRepository.findByTokenTypeAndUserId(tokenType.name(), userEntity.getId());
    }

    @Override
    public int updateTokenConfirmationTime(String token) {
        return tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }

    @Override
    public int updateTokenExpiredTime(String token, LocalDateTime localDateTime) {
        return tokenRepository.updateExpiresAt(token, localDateTime);
    }
}
