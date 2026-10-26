package com.chanko.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of POST /api/auth/login
 *
 * @param login username or email
 * @param password raw password
 */
public record LoginRequest(
  @NotBlank String login,
  @NotBlank String password) {
}
