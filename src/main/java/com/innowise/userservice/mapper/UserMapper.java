package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {

  @Mapping(target = "cards", source = "cards")
  UserDto toDto(User user);

  User toEntity(UserDto dto);
}
