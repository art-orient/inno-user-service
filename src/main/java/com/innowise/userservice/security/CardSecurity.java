package com.innowise.userservice.security;

import com.innowise.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("cardSecurity")
@RequiredArgsConstructor
public class CardSecurity {

  private final PaymentCardService cardService;

  public boolean isOwner(Long cardId) {
    Long ownerId = cardService.getOwnerId(cardId);
    if (ownerId == null) {
      return false;
    }
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AuthUser principal)) {
      return false;
    }
    return ownerId.equals(principal.userId());
  }
}
