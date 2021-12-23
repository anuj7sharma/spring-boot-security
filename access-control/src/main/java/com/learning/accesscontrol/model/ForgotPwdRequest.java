package com.learning.accesscontrol.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@ToString
@Getter
@Setter
@NoArgsConstructor
public class ForgotPwdRequest {
    @NotBlank
    private String email;
}
