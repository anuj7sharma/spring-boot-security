package com.learning.springSecurity.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegisterRequest {
    public static final String EMAIL_REGEX = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";
    @Size(min = 2, max = 30)
    @NotBlank
    private final String firstName;
    @Size(min = 2, max = 30)
    @NotBlank
    private final String lastName;
    @Pattern(regexp = EMAIL_REGEX, message = "Must be a valid email")
    @NotBlank
    private final String email;
    @NotBlank
    @Size(min = 8)
    private String password;
}
