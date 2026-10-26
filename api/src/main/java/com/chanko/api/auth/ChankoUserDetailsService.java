package com.chanko.api.auth;

import com.chanko.api.user.User;
import com.chanko.api.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Connects the users table to Spring Security which during authentication 
 * this looks up the account being logged into
 */
@Service
public class ChankoUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public ChankoUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Gets a users info based on username or email, trimming off by @
   * @param login username or email
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    String trimmed = login.trim();
    User user = (trimmed.contains("@")
      ? userRepository.findByEmailIgnoreCase(trimmed)
      : userRepository.findByUsernameIgnoreCase(trimmed))
      .orElseThrow(() -> new UsernameNotFoundException("No user for login"));

    // OAuth-only accounts have no password and can't use password login
    if (user.getPasswordHash() == null)
      throw new UsernameNotFoundException("User has no password");

    return ChankoPrincipal.from(user);
  }
}
