package com.chanko.api.auth;

import com.chanko.api.auth.dto.LoginRequest;
import com.chanko.api.auth.dto.RegisterRequest;
import com.chanko.api.common.ApiException;
import com.chanko.api.user.User;
import com.chanko.api.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service logic for registering accounts and logging users in
 */
@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  private final SecurityContextHolderStrategy contextHolder = SecurityContextHolder.getContextHolderStrategy();
  private final SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();

  public AuthService(UserRepository userRepository,
    PasswordEncoder passwordEncoder,
    AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
  }

  /**
   * Create a new user and log them in
   * @throws 409 with  DataIntegrityViolationException
   */
  @Transactional
  public User register(RegisterRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    String username = request.username().trim();
    String email = request.email().trim();

    if (userRepository.existsByUsernameIgnoreCase(username))
      throw new ApiException(HttpStatus.CONFLICT, "USERNAME_TAKEN", "That username is already taken");

    if (userRepository.existsByEmailIgnoreCase(email))
      throw new ApiException(HttpStatus.CONFLICT, "EMAIL_TAKEN", "An account with that email already exists");

    User user = new User(username, email, passwordEncoder.encode(request.password()));
    user.recordLogin(Instant.now());
    user = userRepository.saveAndFlush(user);

    ChankoPrincipal principal = ChankoPrincipal.from(user);
    principal.eraseCredentials();
    Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
      principal, null, principal.getAuthorities());
    startSession(auth, httpRequest, httpResponse);

    return user;
  }

  /**
   * Check credentials and save the authenticated SecurityContext into the HTTP session
   * @throws 401 with bad crednetials
   */
  @Transactional
  public User login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    Authentication auth = authenticationManager.authenticate(
      UsernamePasswordAuthenticationToken.unauthenticated(request.login(), request.password()));

    ChankoPrincipal principal = (ChankoPrincipal) auth.getPrincipal();
    User user = userRepository.findById(principal.getId())
      .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
        "Invalid username/email or password"));
    user.recordLogin(Instant.now());

    startSession(auth, httpRequest, httpResponse);
    return user;
  }

  private void startSession(Authentication auth, HttpServletRequest request, HttpServletResponse response) {
    // New session id on login, to prevent session fixation.
    if (request.getSession(false) != null)
      request.changeSessionId();

    SecurityContext context = contextHolder.createEmptyContext();
    context.setAuthentication(auth);
    contextHolder.setContext(context);
    contextRepository.saveContext(context, request, response);
  }

}
