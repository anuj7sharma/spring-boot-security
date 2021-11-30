package com.learning.java.userApp.service;

import com.learning.java.userApp.entity.TokenType;
import com.learning.java.userApp.model.RegisterRequest;

public interface IUserService {
    String register(RegisterRequest request);

    String confirmToken(String token, TokenType tokenType);

    void enableUser(String username);
}
