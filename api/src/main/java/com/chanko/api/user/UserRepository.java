package com.chanko.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Data access layer for User to call queries from the method names
 */
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsernameIgnoreCase(String username);

  Optional<User> findByEmailIgnoreCase(String email);

  boolean existsByUsernameIgnoreCase(String username);

  boolean existsByEmailIgnoreCase(String email);
}
