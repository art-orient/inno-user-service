package by.art.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String surname;

  @Email
  @NotBlank
  private String email;

  private Boolean active;

  @Past
  private LocalDate birthDate;
}
