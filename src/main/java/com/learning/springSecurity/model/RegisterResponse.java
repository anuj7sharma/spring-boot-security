package com.learning.springSecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String message;
}
