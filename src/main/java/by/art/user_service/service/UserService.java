package by.art.user_service.service;

import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;

public interface UserService {

  User create(User user) throws UserServiceException;
}
