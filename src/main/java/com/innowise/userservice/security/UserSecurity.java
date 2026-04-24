package com.innowise.userservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("userSecurity")
public class UserSecurity {

  public boolean isSelf(Long userId) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AuthUser principal)) {
      return false;
    }
    return userId.equals(principal.userId());
  }

  public boolean isSagaDeleteRequest() {
    ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    String header = attrs.getRequest().getHeader("X-Saga-Delete");
    return "true".equals(header);
  }
}
