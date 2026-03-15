package by.art.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentCardDto {

  private Long id;

  @NotBlank(message = "Card number is required")
  @Size(min = 16, max = 16, message = "Card number must be 16 digits")
  private String cardNumber;

  private Boolean active;

  @NotNull(message = "User ID is required")
  private Long userId;
}
