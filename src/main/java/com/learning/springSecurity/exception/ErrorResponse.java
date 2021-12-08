package com.learning.springSecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus httpStatus;
    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp;
}
