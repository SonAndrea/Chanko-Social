package com.chanko.api.common;

import org.springframework.http.HttpStatus;

/**
 * Represents expected errors thrown by services.
 * GlobalExceptionHandler turns it into an ApiError with the given status and code.
 */
public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final String code;

  public ApiException(HttpStatus status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }
}
