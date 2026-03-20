package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
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
   * Retrieves a paginated list of payment cards filtered by user name and surname.
   *
   * @param name     optional filter by user name
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
   * Activates a payment card.
   *
   * @param id the identifier of the payment card to activate
   * @return the activated payment card entity
   */
  PaymentCard activate(Long id);

  /**
   * Deletes a payment card by its identifier.
   *
   * @param id the identifier of the payment card to delete
   * @return the deleted payment card entity
   */
  PaymentCard delete(Long id);
}
