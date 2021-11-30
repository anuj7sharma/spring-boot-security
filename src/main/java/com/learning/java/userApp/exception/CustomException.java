package com.learning.java.userApp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomException {
    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;
}
