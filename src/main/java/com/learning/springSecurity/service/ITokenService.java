package com.learning.springSecurity.service;

import com.learning.springSecurity.entity.TokenEntity;

import java.util.Optional;

public interface ITokenService {
    void saveConfirmationToken(TokenEntity tokenEntity);

    Optional<TokenEntity> getConfirmationToken(String token);

    int updateTokenConfirmationTime(String token);
}
