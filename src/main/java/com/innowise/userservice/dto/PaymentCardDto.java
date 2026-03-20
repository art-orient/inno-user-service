package com.innowise.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Data Transfer Object representing a user's payment card.
 * <p>
 * This DTO is used to transfer payment card information between application layers
 * without exposing internal entity structures. It contains card identification data,
 * validation rules, activation status, and ownership details.
 * </p>
 */
@Data
public class PaymentCardDto implements Serializable {

  /**
   * Unique identifier of the payment card.
   */
  private Long id;

  /**
   * Card number consisting of 16 digits.
   * May include spaces or dashes.
   * Must not be blank and must match the defined pattern.
   */
  @NotBlank(message = "Card number is required")
  @Pattern(
          regexp = "^(?:\\d[ -]?){15}\\d$",
          message = "Card number must contain 16 digits and may include spaces or dashes"
  )
  private String number;

  /**
   * Indicates whether the payment card is active.
   */
  private Boolean active;

  /**
   * Identifier of the user who owns this payment card.
   * Must not be null.
   */
  @NotNull(message = "User ID is required")
  private Long userId;

  /**
   * Full name of the card holder as printed on the card.
   * Must not be blank.
   */
  @NotBlank(message = "Card holder is required")
  private String holder;

  /**
   * Expiration date of the payment card.
   * Must not be null.
   */
  @NotNull(message = "Expiration date is required")
  private LocalDate expirationDate;
}