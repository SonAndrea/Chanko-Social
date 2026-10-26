package com.chanko.api.user.dto;

import com.chanko.api.user.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * Response of admin viewing a user's profile.
 */
public record AdminProfileResponse(
  UUID id,
  String username,
  Role role,
  boolean emailVerified,
  Instant lastLoginAt,
  Instant createdAt) implements ProfileResponse {
}
