package by.art.user_service.service;

import by.art.user_service.dto.UserDto;
import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;
import by.art.user_service.mapper.UserMapper;
import by.art.user_service.repository.UserRepository;
import by.art.user_service.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND = "User not found";
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto create(UserDto userDto) {
    User user = userMapper.toEntity(userDto);
    User userFromDb = userRepository.save(user);
    return userMapper.toDto(userFromDb);
  }

  @Override
  public UserDto getById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    return userMapper.toDto(user);
  }

  @Override
  public Page<UserDto> getAll(String name, String surname, Pageable pageable) {
    Specification<User> spec = UserSpecification.hasName(name)
            .and(UserSpecification.hasSurname(surname));
    return userRepository.findAll(spec, pageable)
            .map(userMapper::toDto);
  }

  @Override
  @Transactional
  public UserDto update(Long id, UserDto dto) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    user.setName(dto.getName());
    user.setSurname(dto.getSurname());
    user.setEmail(dto.getEmail());
    user.setBirthDate(dto.getBirthDate());
    user.setActive(dto.getActive());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void activate(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    user.setActive(true);
  }

  @Override
  @Transactional
  public void deactivate(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    user.setActive(false);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    userRepository.delete(user);
  }
}
