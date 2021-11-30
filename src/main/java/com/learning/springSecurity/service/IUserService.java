package com.learning.springSecurity.service;

import com.learning.springSecurity.entity.TokenType;
import com.learning.springSecurity.model.RegisterRequest;

public interface IUserService {
    String register(RegisterRequest request);

    String confirmToken(String token, TokenType tokenType);

    void enableUser(String username);
}
