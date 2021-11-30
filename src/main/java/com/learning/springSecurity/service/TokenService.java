package com.learning.springSecurity.service;

import com.learning.springSecurity.entity.TokenEntity;
import com.learning.springSecurity.repository.TokenRepository;
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
    public int updateTokenConfirmationTime(String token) {
        return tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}