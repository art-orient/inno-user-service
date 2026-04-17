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
   * @param name     optional filter by username
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
   * Performs a soft delete of a user by deactivating the account.
   * <p>
   * This operation marks the user as inactive without removing the record
   * from the database. It is used for regular business operations when a user
   * or administrator disables an account.
   * </p>
   *
   * @param id the identifier of the user to deactivate
   */
  void deactivate(Long id);

  /**
   * Performs a hard delete of a user by permanently removing the record.
   * <p>
   * This operation is intended exclusively for system-level cleanup scenarios,
   * such as rollback during the registration Saga executed by the API Gateway.
   * It physically deletes the user from the database and must not be used
   * in regular business flows.
   * </p>
   *
   * @param id the identifier of the user to delete permanently
   */
  void hardDelete(Long id);

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