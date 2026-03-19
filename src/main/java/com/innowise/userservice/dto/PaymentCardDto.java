package com.innowise.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PaymentCardDto implements Serializable {

  private Long id;

  @NotBlank(message = "Card number is required")
  @Size(min = 16, max = 16, message = "Card number must be 16 digits")
  private String number;

  private Boolean active;

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotBlank(message = "Card holder is required")
  private String holder;

  @NotNull(message = "Expiration date is required")
  private LocalDate expirationDate;
}
