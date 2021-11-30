package com.learning.springSecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        CustomException customException = new CustomException(badRequest.value(), badRequest.name(),
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(customException, badRequest);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        CustomException customException = new CustomException(notFound.value(), notFound.name(),
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(customException, notFound);
    }

}
