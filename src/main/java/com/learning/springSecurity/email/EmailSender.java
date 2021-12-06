package com.learning.springSecurity.email;

import com.learning.springSecurity.entity.UserEntity;

public interface EmailSender {
    Email buildRegisterConfirmationEmail(UserEntity userEntity, String confirmationLink);

    void send(Email email);
}
