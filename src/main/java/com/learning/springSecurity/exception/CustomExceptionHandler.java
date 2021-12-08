package com.learning.springSecurity.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public final ResponseEntity<Object> handleAllExceptions(ErrorResponse exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getHttpStatus(), exception.getStatusCode(),
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity(errorResponse, exception.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("httpStatus", HttpStatus.BAD_REQUEST);
        body.put("statusCode", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getBindingResult().getFieldError().getDefaultMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(badRequest, badRequest.value(),
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, badRequest);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(notFound, notFound.value(),
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, notFound);
    }
}
