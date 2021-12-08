package com.learning.springSecurity.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Email {
    private String sendTo;
    private String subject;
    private String templateName;
    private Map<String, Object> valueModel;
}
