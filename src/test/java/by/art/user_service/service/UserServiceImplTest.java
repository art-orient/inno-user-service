package by.art.user_service.service;

import by.art.user_service.dto.UserDto;
import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;
import by.art.user_service.mapper.UserMapper;
import by.art.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
  void getById_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);
    UserDto result = userService.getById(1L);
    assertNotNull(result);
    assertEquals("Alex", result.getName());
    verify(userRepository).findById(1L);
  }

  @Test
  void getById_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.getById(1L));
  }

  @Test
  void update_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);
    UserDto result = userService.update(1L, userDto);
    assertEquals("Alex", result.getName());
    assertEquals("Artsikhovich", result.getSurname());
    verify(userRepository).findById(1L);
  }

  @Test
  void update_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.update(1L, userDto));
  }

  @Test
  void delete_success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.delete(1L);
    verify(userRepository).delete(user);
  }

  @Test
  void delete_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.delete(1L));
  }

  @Test
  void activate_success() {
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
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    userService.deactivate(1L);
    assertFalse(user.isActive());
  }

  @Test
  void deactivate_userNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(UserServiceException.class, () -> userService.deactivate(1L));
  }
}
