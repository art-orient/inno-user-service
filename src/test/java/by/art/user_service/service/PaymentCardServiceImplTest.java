package by.art.user_service.service;

import by.art.user_service.dto.PaymentCardDto;
import by.art.user_service.entity.PaymentCard;
import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;
import by.art.user_service.mapper.PaymentCardMapper;
import by.art.user_service.repository.PaymentCardRepository;
import by.art.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  void deactivate_success() {
    when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
    cardService.deactivate(10L);
    assertFalse(card.isActive());
  }

  @Test
  void deactivate_cardNotFound() {
    when(cardRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> cardService.deactivate(10L));
  }

  @Test
  void getByUserId_success() {
    when(userRepository.existsById(1L)).thenReturn(true);
    when(cardRepository.findByUserId(1L)).thenReturn(Collections.singletonList(card));
    when(cardMapper.toDto(card)).thenReturn(cardDto);
    var result = cardService.getByUserId(1L);
    assertEquals(1, result.size());
    assertEquals("1234-5678-9999-0000", result.get(0).getNumber());
  }
}
