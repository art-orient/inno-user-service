package by.art.user_service.service;

import by.art.user_service.dto.PaymentCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {
  PaymentCardDto create(PaymentCardDto dto);

  PaymentCardDto getById(Long id);

  Page<PaymentCardDto> getAll(String name, String surname, Pageable pageable);

  List<PaymentCardDto> getByUserId(Long userId);

  PaymentCardDto update(Long id, PaymentCardDto dto);

  void activate(Long id);

  void delete(Long id);
}
