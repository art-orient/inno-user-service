package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing user-related operations.
 * <p>
 * Provides methods for creating, retrieving, updating, activating,
 * and deleting users, as well as searching users with pagination support.
 * </p>
 */
public interface UserService {

  /**
   * Creates a new user based on the provided data.
   *
   * @param dto the user data to create
   * @return the created user with generated identifier
   */
  UserDto create(UserDto dto);

  /**
   * Retrieves a user by its unique identifier.
   *
   * @param id the identifier of the user
   * @return the user data
   */
  UserDto getById(Long id);

  /**
   * Retrieves a paginated list of users filtered by name and surname.
   *
   * @param name     optional filter by user name
   * @param surname  optional filter by user surname
   * @param pageable pagination and sorting information
   * @return a page of users matching the filters
   */
  Page<UserDto> getAll(String name, String surname, Pageable pageable);

  /**
   * Updates an existing user with new data.
   *
   * @param id  the identifier of the user to update
   * @param dto the updated user data
   * @return the updated user
   */
  UserDto update(Long id, UserDto dto);

  /**
   * Activates a user account.
   *
   * @param id the identifier of the user to activate
   */
  void activate(Long id);

  /**
   * Deletes a user by its identifier.
   *
   * @param id the identifier of the user to delete
   */
  void delete(Long id);

  /**
   * Checks whether a user with the specified identifier exists in the system.
   *
   * <p>This method is primarily used for authorization checks to ensure that
   * access control logic does not operate on non‑existent users, preventing
   * incorrect authorization decisions and avoiding unnecessary exceptions.</p>
   *
   * @param id the unique identifier of the user
   * @return {@code true} if a user with the given ID exists; {@code false} otherwise
   */
  boolean exists(Long id);
}