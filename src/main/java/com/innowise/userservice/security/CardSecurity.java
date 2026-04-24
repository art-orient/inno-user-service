package com.innowise.userservice.security;

import com.innowise.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("cardSecurity")
@RequiredArgsConstructor
public class CardSecurity {

  private final PaymentCardService cardService;

  public boolean isOwner(Long userId, Long cardId) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof Long principal)) {
      return false;
    }
    if (!userId.equals(principal)) {
      return false;
    }
    Long ownerId = cardService.getOwnerId(cardId);
    return ownerId != null && ownerId.equals(userId);
  }
}
