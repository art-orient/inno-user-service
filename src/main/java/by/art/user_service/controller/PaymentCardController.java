package by.art.user_service.controller;

import by.art.user_service.dto.PaymentCardDto;
import by.art.user_service.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class PaymentCardController {
  private final PaymentCardService cardService;

  @PostMapping
  public ResponseEntity<PaymentCardDto> create(@Valid @RequestBody PaymentCardDto dto) {
    PaymentCardDto created = cardService.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(cardService.getById(id));
  }

  @GetMapping
  public ResponseEntity<Page<PaymentCardDto>> getAll(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) String surname,
          Pageable pageable) {
    return ResponseEntity.ok(cardService.getAll(name, surname, pageable));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentCardDto>> getByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(cardService.getByUserId(userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentCardDto> update(@PathVariable Long id, @Valid @RequestBody PaymentCardDto dto) {
    return ResponseEntity.ok(cardService.update(id, dto));
  }

  @PatchMapping("/{id}/activate")
  public ResponseEntity<Void> activate(@PathVariable Long id) {
    cardService.activate(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivate(@PathVariable Long id) {
    cardService.deactivate(id);
    return ResponseEntity.noContent().build();
  }
}
