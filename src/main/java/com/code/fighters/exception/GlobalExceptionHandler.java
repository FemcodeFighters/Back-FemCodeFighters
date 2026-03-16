package com.code.fighters.exception;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.code.fighters.dto.response.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // error 404
    @ExceptionHandler({ UserNotFoundException.class, PlayerNotFoundException.class })
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    // error 409
    @ExceptionHandler({ EmailAlreadyExistsException.class, UserNameAlreadyExistsException.class })
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    // error 401
    @ExceptionHandler({ InvalidPasswordException.class, BadCredentialsException.class })
    public ResponseEntity<ErrorResponseDTO> handleUnauthorized(
            RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    // error 400: validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = Optional.ofNullable(ex.getBindingResult())
                .map(result -> result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .findFirst()
                        .orElse("Validation error"))
                .orElse("Validation error");
        return build(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    // error 500: cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request.getRequestURI());
    }

    // helper
    @SuppressWarnings("null")
    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), message, path, LocalDateTime.now()));
    }
}
