package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserServiceException;
import com.innowise.userservice.mapper.UserMapper;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private UserServiceImpl userService;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Alex");
    user.setSurname("Artsikhovich");
    user.setEmail("orientirik@gmail.com");
    user.setActive(true);
    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setName("Alex");
    userDto.setSurname("Artsikhovich");
    userDto.setEmail("orientirik@gmail.com");
    userDto.setActive(true);
  }

  @Test
  void create_createsNewUserWhenNotExists() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);
    UserDto result = userService.create(userDto);
    assertNotNull(result);
    assertTrue(user.isActive());
    verify(userRepository).findById(1L);
    verify(userMapper).toEntity(userDto);
    verify(userRepository).save(user);
    verify(userMapper).toDto(user);
  }

  @Test
  void create_throwsExceptionWhenUserAlreadyExists() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThrows(UserServiceException.class, () -> userService.create(userDto));
    verify(userRepository).findById(1L);
    verify(userRepository, never()).save(any());
  }

  @Test
  void create_throwsExceptionWhenIdIsNull() {
    userDto.setId(null);
    assertThrows(UserServiceException.class, () -> userService.create(userDto));
    verify(userRepository, never()).findById(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void getById_success() {
    when(userRepository.findUserWithCardsById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);
    UserDto result = userService.getById(1L);
    assertNotNull(result);
    verify(userRepository).findUserWithCardsById(1L);
  }

  @Test
  void getById_userNotFound() {
    when(userRepository.findUserWithCardsById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.getById(1L));
  }

  @Test
  void getById_inactiveUser() {
    user.setActive(false);
    when(userRepository.findUserWithCardsById(1L)).thenReturn(Optional.of(user));
    assertThrows(UserServiceException.class, () -> userService.getById(1L));
  }

  @Test
  void getAll_withoutFilters() {
    Pageable pageable = mock(Pageable.class);
    Page<User> page = Page.empty();
    when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<UserDto> result = userService.getAll(null, null, pageable);
    assertNotNull(result);
    verify(userRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getAll_withFilters() {
    Pageable pageable = mock(Pageable.class);
    Page<User> page = Page.empty();
    when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
    Page<UserDto> result = userService.getAll("Alex", "Artsikhovich", pageable);
    assertNotNull(result);
    verify(userRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void update_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);
    UserDto result = userService.update(1L, userDto);
    assertEquals("Alex", result.getName());
    verify(userRepository).findById(1L);
  }

  @Test
  void update_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.update(1L, userDto));
  }

  @Test
  void update_updatesBirthDate() {
    userDto.setBirthDate(LocalDate.of(1990, 1, 1));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);
    userService.update(1L, userDto);
    assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
  }

  @Test
  void activate_success() {
    user.setActive(false);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.activate(1L);
    assertTrue(user.isActive());
  }

  @Test
  void activate_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.activate(1L));
  }

  @Test
  void deactivate_success() {
    user.setActive(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.deactivate(1L);
    assertFalse(user.isActive());
  }

  @Test
  void deactivate_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.deactivate(1L));
  }

  @Test
  void hardDelete_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.hardDelete(1L);
    verify(userRepository).delete(user);
  }

  @Test
  void hardDelete_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.hardDelete(1L));
  }

  @Test
  void exists_true() {
    when(userRepository.existsById(1L)).thenReturn(true);
    assertTrue(userService.exists(1L));
  }

  @Test
  void exists_false() {
    when(userRepository.existsById(1L)).thenReturn(false);
    assertFalse(userService.exists(1L));
  }
}