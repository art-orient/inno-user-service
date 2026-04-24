package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/cards")
@RequiredArgsConstructor
public class PaymentCardController {

  private final PaymentCardService cardService;

  @PreAuthorize("hasRole('ADMIN') or #dto.userId == authentication.principal")
  @PostMapping
  public ResponseEntity<PaymentCardDto> create(@PathVariable Long userId, @Valid @RequestBody PaymentCardDto dto) {
    dto.setUserId(userId);
    PaymentCardDto created = cardService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PreAuthorize("hasRole('ADMIN') or @cardSecurity.isOwner(#userId, #cardId)")
  @GetMapping("/{cardId}")
  public ResponseEntity<PaymentCardDto> getById(@PathVariable Long userId, @PathVariable Long cardId) {
    return ResponseEntity.ok(cardService.getById(cardId));
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
  @GetMapping("/list")
  public ResponseEntity<List<PaymentCardDto>> getByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(cardService.getByUserId(userId));
  }

  @PreAuthorize("hasRole('ADMIN') or @cardSecurity.isOwner(#userId, #cardId)")
  @PutMapping("/{cardId}")
  public ResponseEntity<PaymentCardDto> update(@PathVariable Long userId, @PathVariable Long cardId,
                                               @Valid @RequestBody PaymentCardDto dto) {
    return ResponseEntity.ok(cardService.update(cardId, dto));
  }

  @PreAuthorize("hasRole('ADMIN') or @cardSecurity.isOwner(#userId, #cardId)")
  @PatchMapping("/{cardId}/activate")
  public ResponseEntity<Void> activate(@PathVariable Long userId, @PathVariable Long cardId) {
    cardService.activate(cardId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN') or @cardSecurity.isOwner(#userId, #cardId)")
  @DeleteMapping("/{cardId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long userId, @PathVariable Long cardId) {
    cardService.delete(cardId);
  }
}
