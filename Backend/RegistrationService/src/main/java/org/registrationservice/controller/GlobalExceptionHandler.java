package org.registrationservice.controller;

import org.registrationservice.exception.ExpiredConfirmationTokenException;
import org.registrationservice.exception.InvalidParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        // Log the exception details here as needed
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: " + e.getMessage());
    }

    @ExceptionHandler(ExpiredConfirmationTokenException.class)
    public ResponseEntity<String> handleExpiredConfirmationTokenException(ExpiredConfirmationTokenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Expired Confirmation Token: " + e.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameter: " + e.getMessage());
    }
}