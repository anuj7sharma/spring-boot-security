package com.learning.accesscontrol.service;

import com.learning.accesscontrol.entity.TokenType;
import com.learning.accesscontrol.entity.UserEntity;
import com.learning.accesscontrol.model.*;

import java.util.List;

public interface IUserService {
    RegisterResponse register(RegisterRequest request);

    MessageResponse confirmToken(String token, TokenType tokenType);

    MessageResponse forgotPassword(ForgotPwdRequest request);

    MessageResponse changePassword(ChangePwdRequest request);

    void enableUser(String username);

    List<UserEntity> getAllUser();
}
