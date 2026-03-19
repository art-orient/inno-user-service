package com.innowise.userservice.service;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {
  PaymentCardDto create(PaymentCardDto dto);

  PaymentCardDto getById(Long id);

  Page<PaymentCardDto> getAll(String name, String surname, Pageable pageable);

  List<PaymentCardDto> getByUserId(Long userId);

  PaymentCardDto update(Long id, PaymentCardDto dto);

  PaymentCard activate(Long id);

  PaymentCard delete(Long id);
}
