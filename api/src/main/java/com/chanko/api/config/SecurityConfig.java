package com.chanko.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * Central Spring Security configuration which uses session-based authorization.
 * Each login stores the SecurityContext in the HTTP session and the browser
 * keeps a JSESSIONID cookie.
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize for admin-only endpoints
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
        // /me must come before the /{username} wildcard
        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/users/*").permitAll()
        .requestMatchers("/actuator/health", "/error").permitAll()
        .anyRequest().authenticated())
      // Return a bare 401 instead of redirecting to a login page
      .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
      .logout(logout -> logout
        .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults()
          .matcher(HttpMethod.POST, "/api/auth/logout"))
        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
        .deleteCookies("JSESSIONID"));
    return http.build();
  }

  /** 
   * Delegating encoder: stores hashes as "{bcrypt}..." so the algorithm can be upgraded later 
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * Built by Spring Boot from the ChankoUserDetailsService and PasswordEncoder beans.
   * AuthService uses it to check login credentials.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
