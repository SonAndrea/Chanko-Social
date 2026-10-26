package com.chanko.api.auth;

import com.chanko.api.auth.dto.LoginRequest;
import com.chanko.api.auth.dto.RegisterRequest;
import com.chanko.api.user.User;
import com.chanko.api.user.UserService;
import com.chanko.api.user.dto.PrivateProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * AuthController manages the endpoints for creating and logging in users.
 * 
 * Register and login respond with a user's private profile response to prevent
 * multiple requests. Logout is managed by Spring Security in Security Config.
*/
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  /**
   * POST /api/auth/register: create an account and log the new user in
   * Code: 201 on success
   */
  @PostMapping("/register")
  public ResponseEntity<PrivateProfileResponse> register(
    @Valid @RequestBody RegisterRequest body,
    HttpServletRequest request,
    HttpServletResponse response) {
    User user = authService.register(body, request, response);
    return ResponseEntity
      .created(URI.create("/api/users/" + user.getUsername()))
      .body(userService.toPrivate(user));
  }

  /**
   * POST /api/auth/login: check credentials and start a session
   * Code: 401 on bad credentials
   */
  @PostMapping("/login")
  public ResponseEntity<PrivateProfileResponse> login(
    @Valid @RequestBody LoginRequest body,
    HttpServletRequest request,
    HttpServletResponse response) {
    User user = authService.login(body, request, response);
    return ResponseEntity.ok(userService.toPrivate(user));
  }
}
