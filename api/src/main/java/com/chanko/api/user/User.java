package com.chanko.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for the users table. Never return this from a controller. 
 * Map it to one of the profile DTOs so private fields can't leak.
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String email;

  /** 
   * Nullable: accounts created through OAuth won't have a password. 
   */
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  private Instant emailVerifiedAt;

  private Instant lastLoginAt;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  /** 
   * For JPA only
   */
  protected User() {
  }

  /** 
   * A new password-based account with the default USER role 
   */
  public User(String username, String email, String passwordHash) {
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = Role.USER;
    this.createdAt = Instant.now();
  }

  public void recordLogin(Instant at) {
    this.lastLoginAt = at;
  }

  public boolean isEmailVerified() {
    return emailVerifiedAt != null;
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public Instant getEmailVerifiedAt() {
    return emailVerifiedAt;
  }

  public Instant getLastLoginAt() {
    return lastLoginAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
