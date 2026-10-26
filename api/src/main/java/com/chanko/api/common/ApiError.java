package com.chanko.api.common;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a uniform error body returned by every failed API call
 * 
 * @param status HTTP status code
 * @param error short machine code message
 * @param message human-readable message
 * @param fieldErrors per-field validation messages (field name -> message)
 * @param timestamp when the error occurred
 */
public record ApiError(
  int status,
  String error,
  String message,
  Map<String, String> fieldErrors,
  Instant timestamp) {
}
