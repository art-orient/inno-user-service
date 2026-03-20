package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserServiceException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {
  @Mock
  private PaymentCardRepository cardRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PaymentCardMapper cardMapper;
  @InjectMocks
  private PaymentCardServiceImpl cardService;
  private User user;
  private PaymentCard card;
  private PaymentCardDto cardDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Alex");
    user.setSurname("Artsikhovich");
    user.setEmail("orientirik@gmail.com");
    user.setActive(true);

    card = new PaymentCard();
    card.setId(10L);
    card.setNumber("1234-5678-9999-0000");
    card.setActive(true);
    card.setUser(user);

    cardDto = new PaymentCardDto();
    cardDto.setId(10L);
    cardDto.setNumber("1234-5678-9999-0000");
    cardDto.setActive(true);
    cardDto.setUserId(1L);
  }

  @Test
  void create_success() {
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
    when(cardMapper.toEntity(any(PaymentCardDto.class))).thenReturn(card);
    when(cardRepository.save(any(PaymentCard.class))).thenReturn(card);
    when(cardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);
    PaymentCardDto result = cardService.create(cardDto);
    assertNotNull(result);
    assertEquals("1234-5678-9999-0000", result.getNumber());
    verify(cardRepository).save(any(PaymentCard.class));
  }

  @Test
  void create_userNotFound() {
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.create(cardDto));
  }

  @Test
  void create_cardLimitExceeded() {
    user.setCards(Collections.nCopies(5, new PaymentCard())); // имитируем 5 карт
    when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
    assertThrows(UserServiceException.class, () -> cardService.create(cardDto));
  }

  @Test
  void getById_success() {
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    when(cardMapper.toDto(card)).thenReturn(cardDto);
    PaymentCardDto result = cardService.getById(10L);
    assertNotNull(result);
    assertEquals("1234-5678-9999-0000", result.getNumber());
  }

  @Test
  void getById_cardNotFound() {
    when(cardRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.getById(10L));
  }

  @Test
  void getById_inactiveCard() {
    card.setActive(false);
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    assertThrows(UserServiceException.class, () -> cardService.getById(10L));
  }

  @Test
  void getAll_success() {
    Pageable pageable = mock(Pageable.class);
    Page<PaymentCard> page = Page.empty();
    when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<PaymentCardDto> result = cardService.getAll("Alex", "Artsikhovich", pageable);
    assertNotNull(result);
    verify(cardRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void update_success() {
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    when(cardMapper.toDto(card)).thenReturn(cardDto);
    PaymentCardDto result = cardService.update(10L, cardDto);
    assertEquals("1234-5678-9999-0000", result.getNumber());
    verify(cardRepository).findById(10L);
  }

  @Test
  void update_cardNotFound() {
    when(cardRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.update(10L, cardDto));
  }

  @Test
  void activate_success() {
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    cardService.activate(10L);
    assertTrue(card.isActive());
  }

  @Test
  void activate_cardNotFound() {
    when(cardRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.activate(10L));
  }

  @Test
  void getByUserId_success() {
    when(userRepository.existsById(1L)).thenReturn(true);
    when(cardRepository.findAllByUserIdAndActiveTrue(1L)).thenReturn(Collections.singletonList(card));
    when(cardMapper.toDto(card)).thenReturn(cardDto);
    var result = cardService.getByUserId(1L);
    assertEquals(1, result.size());
    assertEquals("1234-5678-9999-0000", result.get(0).getNumber());
  }

  @Test
  void getByUserId_userNotFound() {
    when(userRepository.existsById(1L)).thenReturn(false);
    assertThrows(UserServiceException.class, () -> cardService.getByUserId(1L));
  }

  @Test
  void delete_success() {
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    PaymentCard result = cardService.delete(10L);
    assertFalse(result.isActive());
    verify(cardRepository).findById(10L);
  }

  @Test
  void delete_cardNotFound() {
    when(cardRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.delete(10L));
  }
}
