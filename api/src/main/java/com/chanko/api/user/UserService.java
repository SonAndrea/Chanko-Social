package com.chanko.api.user;

import com.chanko.api.auth.ChankoPrincipal;
import com.chanko.api.common.ApiException;
import com.chanko.api.user.dto.AdminProfileResponse;
import com.chanko.api.user.dto.PrivateProfileResponse;
import com.chanko.api.user.dto.ProfileResponse;
import com.chanko.api.user.dto.PublicProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for profile lookups, plus the rules for which view of a user each viewer gets:
 *
 *  Viewer is the profile owner -> PrivateProfileResponse (self provided information)
 *  Viewer is an ADMIN -> public fields + extra account info
 *  Viewer is neither -> name only
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /** 
   * The logged-in user's own profile (GET /api/users/me)
   */
  @Transactional(readOnly = true)
  public PrivateProfileResponse getOwnProfile(UUID userId) {
    return userRepository.findById(userId)
      .map(this::toPrivate)
      .orElseThrow(UserService::notFound);
  }

  /**
   * A user's profile, in the view that matches who is asking
   *
   * @param viewer the logged-in principal, or null when anonymous
   */
  @Transactional(readOnly = true)
  public ProfileResponse getProfile(String username, ChankoPrincipal viewer) {
    User user = userRepository.findByUsernameIgnoreCase(username)
      .orElseThrow(UserService::notFound);

    if (viewer != null && viewer.getId().equals(user.getId())) {
      return toPrivate(user);
    }
    if (viewer != null && viewer.isAdmin()) {
      return toAdmin(user);
    }
    return toPublic(user);
  }

  public PublicProfileResponse toPublic(User user) {
    return new PublicProfileResponse(user.getUsername());
  }

  public AdminProfileResponse toAdmin(User user) {
    return new AdminProfileResponse(
      user.getId(),
      user.getUsername(),
      user.getRole(),
      user.isEmailVerified(),
      user.getLastLoginAt(),
      user.getCreatedAt());
  }

  public PrivateProfileResponse toPrivate(User user) {
    return new PrivateProfileResponse(
      user.getId(),
      user.getUsername(),
      user.getEmail(),
      user.getRole(),
      user.isEmailVerified(),
      user.getCreatedAt());
  }

  private static ApiException notFound() {
    return new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found");
  }
}
