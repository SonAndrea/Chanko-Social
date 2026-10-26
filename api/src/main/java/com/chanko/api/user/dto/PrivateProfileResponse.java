package com.chanko.api.user.dto;

import com.chanko.api.user.Role;

import java.time.Instant;
import java.util.UUID;

/**
 * Response of a user viewing a their own profile.
 */
public record PrivateProfileResponse(
  UUID id,
  String username,
  String email,
  Role role,
  boolean emailVerified,
  Instant createdAt) implements ProfileResponse {
}
