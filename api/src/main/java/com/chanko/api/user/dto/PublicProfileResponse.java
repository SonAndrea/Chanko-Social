package com.chanko.api.user.dto;

/**
 * Response of any normal user viewing another user's profile.
 */
public record PublicProfileResponse(
  String username) implements ProfileResponse {
}
