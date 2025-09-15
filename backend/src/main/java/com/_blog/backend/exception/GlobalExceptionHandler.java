package com._blog.backend.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest req) {
        ErrorResponse err = new ErrorResponse(
            Instant.now().toString(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }
}
