package com.learning.springSecurity.service;

import com.learning.springSecurity.entity.TokenEntity;

import java.util.Optional;

public interface ITokenService {
    /**
     * This method responsible to persist the confirmation token
     *
     * @param tokenEntity object of token
     */
    void saveConfirmationToken(TokenEntity tokenEntity);

    /**
     * This method is used to get the confirmation token with Optional
     *
     * @param token token shared with the user
     * @return Optional object of TokenEntity
     */
    Optional<TokenEntity> getConfirmationToken(String token);

    /**
     * This method is responsible to update the token when user click the confirmation link
     *
     * @param token token shared to used
     * @return int value
     */
    int updateTokenConfirmationTime(String token);
}
