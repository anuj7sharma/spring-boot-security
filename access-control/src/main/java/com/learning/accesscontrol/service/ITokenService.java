package com.learning.accesscontrol.service;

import com.learning.accesscontrol.entity.TokenEntity;
import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;

import java.time.LocalDateTime;
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
     * This method is responsible to return the token entity object on basis of token type and userId
     *
     * @param tokenType  token type
     * @param userEntity userEntity object
     * @return object of tokenEntity
     */
    Optional<TokenEntity> getTokenByTypeAndUserId(TokenType tokenType, UserEntity userEntity);

    /**
     * This method is responsible to update the token when user click the confirmation link
     *
     * @param token token shared to used
     * @return int value
     */
    int updateTokenConfirmationTime(String token);

    /**
     * This method is responsible to update the expired_at column if the updated_at is null and user tries to
     * click forgot-password link again
     *
     * @param token     token shared to used
     * @param expiredAt expired time
     * @return int value
     */
    int updateTokenExpiredTime(String token, LocalDateTime expiredAt);
}
