package com.innowise.userservice.security;

import com.innowise.userservice.dto.UserDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

  public boolean isSelf(Long userId) {
    AuthUser auth = (AuthUser) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    return userId.equals(auth.getUserId());
  }

  public boolean isSelfCreate(UserDto dto) {
    var auth = (AuthUser) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
    return dto.getId() != null && dto.getId().equals(auth.getUserId());
  }
}
