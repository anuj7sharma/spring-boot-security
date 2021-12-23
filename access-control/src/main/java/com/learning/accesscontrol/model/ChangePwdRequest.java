package com.learning.accesscontrol.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChangePwdRequest {
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    private String token;
}
