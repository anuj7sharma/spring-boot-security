package com.learning.accesscontrol.service;

import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.model.MessageResponse;
import com.learning.accesscontrol.model.RegisterRequest;
import com.learning.accesscontrol.model.RegisterResponse;

import java.util.List;

public interface IUserService {
    RegisterResponse register(RegisterRequest request);

    MessageResponse confirmToken(String token, TokenType tokenType);

    void enableUser(String username);

    List<UserEntity> getAllUser();
}
