package com.innowise.userservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

  public boolean isSelf(Long userId) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AuthUser principal)) {
      return false;
    }
    return userId.equals(principal.userId());
  }
}
