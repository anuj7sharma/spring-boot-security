package com.learning.springSecurity.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Email {
    private String to;
    private String from;
    private String subject;
    private String content;
    private String templateName;
    private Map<String, Object> model;
}
