package com.learning.springSecurity.email;

import javax.mail.MessagingException;

public interface EmailSender {
    void send(Email email) throws MessagingException;
}
