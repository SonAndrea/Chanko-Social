package com.chanko.api.user;

import com.chanko.api.auth.ChankoPrincipal;
import com.chanko.api.user.dto.PrivateProfileResponse;
import com.chanko.api.user.dto.ProfileResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Profile endpoints. The visibility rules live in UserService.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /** 
   * GET /api/users/me: the current user's private profile
   * @throws 401 when not logged in
   */
  @GetMapping("/me")
  public PrivateProfileResponse me(@AuthenticationPrincipal ChankoPrincipal principal) {
    return userService.getOwnProfile(principal.getId());
  }

  /**
   * GET /api/users/{username}: Returns the public, admin, or private view depending on the viewer
   */
  @GetMapping("/{username}")
  public ProfileResponse byUsername(@PathVariable String username,
    @AuthenticationPrincipal ChankoPrincipal principal) {
    return userService.getProfile(username, principal);
  }
}
