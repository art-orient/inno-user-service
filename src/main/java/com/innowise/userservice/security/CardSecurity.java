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
    AuthUser auth = (AuthUser) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    return ownerId.equals(auth.getUserId());
  }
}
