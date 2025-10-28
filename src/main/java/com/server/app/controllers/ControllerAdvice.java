package com.server.app.controllers;

import com.server.app.dto.ExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvice {

    // 🔹 Excepciones de JWT expirado
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwt(ExpiredJwtException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Token expirado: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 🔹 Excepciones de JWT inválido
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleJwtException(JwtException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Token inválido: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 🔹 Credenciales inválidas
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 🔹 Acceso denegado (roles/permisos insuficientes)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDenied(AccessDeniedException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setMessage("Acceso denegado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 🔹 Validaciones de DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        response.setMessage(message.isEmpty() ? ex.getMessage() : message);
        return ResponseEntity.badRequest().body(response);
    }

    // 🔹 Parámetros faltantes en request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Parámetro requerido faltante: " + ex.getParameterName());
        return ResponseEntity.badRequest().body(response);
    }

    // 🔹 Tipo de parámetro inválido
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Parámetro inválido: " + ex.getName());
        return ResponseEntity.badRequest().body(response);
    }

    // 🔹 Excepciones de negocio / runtime
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(RuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // 🔹 Cualquier otra excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Error interno del servidor: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
