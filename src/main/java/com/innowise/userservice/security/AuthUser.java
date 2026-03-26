package com.innowise.userservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents authenticated user information extracted from JWT.
 * Stored inside SecurityContext for authorization checks.
 */
@Getter
@ToString
@AllArgsConstructor
public class AuthUser {

  private final Long userId;
  private final String role;
}
