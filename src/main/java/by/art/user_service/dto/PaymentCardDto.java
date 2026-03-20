package by.art.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentCardDto {

  private Long id;

  @NotBlank
  @Size(min = 16, max = 16)
  private String cardNumber;

  private Boolean active;

  @NotNull
  private Long userId;
}
