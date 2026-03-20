package by.art.user_service.service;

import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;
import by.art.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public User create(User user) throws UserServiceException{
    if (user.getCards().size() >= 5) {
      throw new UserServiceException("User can't have more than 5 cards");
    }
    return userRepository.save(user);
  }
}
