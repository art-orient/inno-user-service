package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.UserServiceException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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


  @Cacheable(value = "user", key = "#id")
  @Override
  public UserDto getById(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    if (!user.isActive()) {
      throw new UserServiceException(USER_NOT_FOUND);
    }
    return userMapper.toDto(user);
  }

  @Override
  public Page<UserDto> getAll(String name, String surname, Pageable pageable) {
    Specification<User> spec = (root, query, cb) -> cb.isTrue(root.get("active"));
    if (name != null) {
      spec = spec.and(UserSpecification.hasName(name));
    }
    if (surname != null) {
      spec = spec.and(UserSpecification.hasSurname(surname));
    }
    return userRepository.findAll(spec, pageable)
            .map(userMapper::toDto);
  }

  @CacheEvict(value = "user", key = "#id")
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

  @CacheEvict(value = "user", key = "#id")
  @Override
  @Transactional
  public void activate(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    user.setActive(true);
  }

  @CacheEvict(value = "user", key = "#id")
  @Override
  @Transactional
  public void delete(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    user.setActive(false);
  }
}
