package com.chanko.api.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts exceptions thrown by controllers and services into ApiError responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApi(ApiException ex) {
    return error(ex.getStatus(), ex.getCode(), ex.getMessage(), Map.of());
  }

  /** 
   * Bean Validation failures on @Valid request bodies
   * @throws 400 with fieldErrors populated
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
    }
    return error(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Some fields are invalid", fieldErrors);
  }

  /**
   * Missing or malformed JSON body 
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
    return error(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body is missing or malformed", Map.of());
  }

  /** 
   * Wrong username/password on login 
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
    return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid username/email or password", Map.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
    return error(HttpStatus.FORBIDDEN, "FORBIDDEN", "You don't have access to this resource", Map.of());
  }

  /** 
   * Unique index violation that slipped past the exists-checks (two signups racing)   
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException ex) {
    return error(HttpStatus.CONFLICT, "ACCOUNT_EXISTS", "That username or email is already in use", Map.of());
  }

  /** 
   * Fallback error where Spring MVC keeps own error code 
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleOther(Exception ex) {
    if (ex instanceof ErrorResponse er) {
      HttpStatusCode status = er.getStatusCode();
      return error(status, "HTTP_" + status.value(), er.getBody().getTitle(), Map.of());
    }
    log.error("Unhandled exception", ex);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Something went wrong", Map.of());
  }

  private static ResponseEntity<ApiError> error(HttpStatusCode status, String code, String message,
    Map<String, String> fieldErrors) {
    ApiError body = new ApiError(status.value(), code, message, fieldErrors, Instant.now());
    return ResponseEntity.status(status).body(body);
  }
}
