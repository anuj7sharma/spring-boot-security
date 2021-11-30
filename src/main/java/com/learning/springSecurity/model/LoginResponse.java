package com.learning.springSecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Data
public class LoginResponse {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String accessToken;
    private final String refreshToken;
}
