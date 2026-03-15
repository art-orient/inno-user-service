package by.art.user_service.mapper;

import by.art.user_service.dto.PaymentCardDto;
import by.art.user_service.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(source = "user.id", target = "userId")
  PaymentCardDto toDto(PaymentCard card);

  @Mapping(source = "userId", target = "user.id")
  PaymentCard toEntity(PaymentCardDto dto);
}
