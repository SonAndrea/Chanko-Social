package com.chanko.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/auth/register
 * @param username restricted to 3 to 20 alphanumeric characters or underscore 
 * @param email string of email matching email struct
 * @param password string of password with min and max bytes according to BCrypt's max bytes
 */
public record RegisterRequest(
  @NotBlank @Pattern(regexp = "^[A-Za-z0-9_]{3,20}$", message = "must be 3-20 characters: letters, numbers, or underscores") String username,
  @NotBlank @Email @Size(max = 254) String email,
  @NotBlank @Size(min = 8, max = 72) String password) {
}
