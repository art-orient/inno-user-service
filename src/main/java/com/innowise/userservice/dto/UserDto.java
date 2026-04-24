package com.innowise.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object representing a user within the system.
 * <p>
 * This DTO is used to transfer user-related data between layers of the application
 * without exposing internal entity structures. It contains personal information,
 * contact details, activation status, birth date validation, and a list of associated
 * payment cards.
 * </p>
 */
@Data
public class UserDto implements Serializable {

  /**
   * Unique identifier of the user.
   */
  @NotNull
  private Long id;

  /**
   * User's first name.
   * Must not be blank.
   */
  @NotBlank(message = "Name is required")
  private String name;

  /**
   * User's last name.
   * Must not be blank.
   */
  @NotBlank(message = "Surname is required")
  private String surname;

  /**
   * User's email address.
   * Must be a valid email format and must not be blank.
   */
  @Email(message = "Email is invalid")
  @NotBlank(message = "Email is required")
  private String email;

  /**
   * Indicates whether the user account is active.
   */
  private Boolean active;

  /**
   * User's birth date.
   * Must be a date in the past.
   */
  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

  /**
   * List of payment cards associated with the user.
   */
  private List<PaymentCardDto> cards;
}