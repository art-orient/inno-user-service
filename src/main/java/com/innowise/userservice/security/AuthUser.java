package com.innowise.userservice.security;

/**
 * Represents authenticated user information extracted from JWT.
 * Stored inside SecurityContext for authorization checks.
 */
public record AuthUser(Long userId, String role) {}
