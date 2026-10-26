package com.chanko.api.auth;

import com.chanko.api.user.Role;
import com.chanko.api.user.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The authenticated user as Spring Security sees it which is stored in session
 * which is used wiht the controller.
 */
public class ChankoPrincipal implements UserDetails, CredentialsContainer {

  @Serial
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final String username;
  private String passwordHash; // cleared by eraseCredentials() after authentication
  private final Role role;

  public ChankoPrincipal(UUID id, String username, String passwordHash, Role role) {
    this.id = id;
    this.username = username;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public static ChankoPrincipal from(User user) {
    return new ChankoPrincipal(user.getId(), user.getUsername(), user.getPasswordHash(), user.getRole());
  }

  public UUID getId() {
    return id;
  }

  public Role getRole() {
    return role;
  }

  public boolean isAdmin() {
    return role == Role.ADMIN;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return username;
  }

  /**  
   * Keeps the password hash out of the session once login has succeeded. 
   */
  @Override
  public void eraseCredentials() {
    this.passwordHash = null;
  }
}
