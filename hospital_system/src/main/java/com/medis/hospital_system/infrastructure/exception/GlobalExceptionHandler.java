package com.medis.hospital_system.infrastructure.exception;

import com.medis.hospital_system.domain.exception.AppointmentConflictException;
import com.medis.hospital_system.domain.exception.DuplicateResourceException;
import com.medis.hospital_system.domain.exception.InsufficientStockException;
import com.medis.hospital_system.domain.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Auth errors ───────────────────────────────────────────────────────────

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleBadCredentials(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password"));
    }

    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<Map<String, String>> handleAccountStatus(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Account is disabled or locked"));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Token is invalid or expired"));
    }

    // ── Domain errors ─────────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(AppointmentConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)   // ← ADD THIS
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid value",
                        (a, b) -> a
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}