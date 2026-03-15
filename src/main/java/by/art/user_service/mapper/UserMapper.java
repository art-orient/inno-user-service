package by.art.user_service.mapper;

import by.art.user_service.dto.UserDto;
import by.art.user_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);

  User toEntity(UserDto dto);
}
