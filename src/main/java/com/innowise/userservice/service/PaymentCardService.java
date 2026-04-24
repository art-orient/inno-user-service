package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing user payment cards.
 * <p>
 * Provides operations for creating, retrieving, updating, activating,
 * and deleting payment cards, as well as searching and fetching cards
 * associated with a specific user.
 * </p>
 */
public interface PaymentCardService {

  /**
   * Creates a new payment card based on the provided data.
   *
   * @param dto the payment card data to create
   * @return the created payment card DTO
   */
  PaymentCardDto create(PaymentCardDto dto);

  /**
   * Retrieves a payment card by its unique identifier.
   *
   * @param id the identifier of the payment card
   * @return the payment card DTO
   */
  PaymentCardDto getById(Long id);

  /**
   * Retrieves a paginated list of payment cards filtered by username and surname.
   *
   * @param name     optional filter by username
   * @param surname  optional filter by user surname
   * @param pageable pagination and sorting information
   * @return a page of payment card DTOs matching the filters
   */
  Page<PaymentCardDto> getAll(String name, String surname, Pageable pageable);

  /**
   * Retrieves all payment cards belonging to a specific user.
   *
   * @param userId the identifier of the user
   * @return a list of payment card DTOs associated with the user
   */
  List<PaymentCardDto> getByUserId(Long userId);

  /**
   * Updates an existing payment card with new data.
   *
   * @param id  the identifier of the payment card to update
   * @param dto the updated payment card data
   * @return the updated payment card DTO
   */
  PaymentCardDto update(Long id, PaymentCardDto dto);

  /**
   * Marks the specified payment card as active.
   * <p>
   * This operation performs a soft activation by setting {@code active = true}.
   * The card remains in the database and retains all associated data.
   *
   * @param id the identifier of the payment card to activate
   */
  void activate(Long id);

  /**
   * Deactivates the specified payment card.
   * <p>
   * This operation performs a soft delete by setting {@code active = false}.
   * The card is not removed from the database and can be reactivated later.
   *
   * @param id the identifier of the payment card to deactivate
   */
  void delete(Long id);

  /**
   * Retrieves the identifier of the user who owns the specified payment card.
   *
   * <p>This method is primarily used for authorization checks, allowing the
   * system to verify whether the authenticated user has access rights to
   * perform operations on the given card.</p>
   *
   * @param cardId the unique identifier of the payment card
   * @return the ID of the user who owns the card
   * @throws jakarta.persistence.EntityNotFoundException if the card does not exist
   */
  Long getOwnerId(Long cardId);
}
