package com.learning.java.userApp.service;

import com.learning.java.userApp.entity.TokenEntity;

import java.util.Optional;

public interface ITokenService {
    void saveConfirmationToken(TokenEntity tokenEntity);

    Optional<TokenEntity> getConfirmationToken(String token);

    int updateTokenConfirmationTime(String token);
}
