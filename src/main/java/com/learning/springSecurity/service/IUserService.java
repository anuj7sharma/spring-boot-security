package com.learning.springSecurity.service;

import com.learning.springSecurity.entity.TokenType;
import com.learning.springSecurity.entity.UserEntity;
import com.learning.springSecurity.model.MessageResponse;
import com.learning.springSecurity.model.RegisterRequest;
import com.learning.springSecurity.model.RegisterResponse;

import java.util.List;

public interface IUserService {
    RegisterResponse register(RegisterRequest request);

    MessageResponse confirmToken(String token, TokenType tokenType);

    void enableUser(String username);

    List<UserEntity> getAllUser();
}
