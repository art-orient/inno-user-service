package com.innowise.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserDto implements Serializable {

  private Long id;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Surname is required")
  private String surname;

  @Email(message = "Email is invalid")
  @NotBlank(message = "Email is required")
  private String email;

  private Boolean active;

  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;
}
