package by.art.user_service.service;

import by.art.user_service.dto.PaymentCardDto;
import by.art.user_service.entity.PaymentCard;
import by.art.user_service.entity.User;
import by.art.user_service.exception.UserServiceException;
import by.art.user_service.mapper.PaymentCardMapper;
import by.art.user_service.repository.PaymentCardRepository;
import by.art.user_service.repository.UserRepository;
import by.art.user_service.repository.specification.PaymentCardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService{

  public static final String USER_NOT_FOUND = "User not found";
  public static final String CARD_NOT_FOUND = "Card not found";
  public static final String CARD_LIMIT_EXCEEDED = "User cannot have more than 5 cards";
  private final PaymentCardRepository cardRepository;
  private final UserRepository userRepository;
  private final PaymentCardMapper cardMapper;

  @Transactional
  @CacheEvict(value = "user", key = "#dto.userId")
  @Override
  public PaymentCardDto create(PaymentCardDto dto) {
    User user = userRepository.findByIdForUpdate(dto.getUserId())
            .orElseThrow(() -> new UserServiceException(USER_NOT_FOUND));
    if (user.getCards().size() >= 5) {
      throw new UserServiceException(CARD_LIMIT_EXCEEDED);
    }
    PaymentCard card = cardMapper.toEntity(dto);
    card.setUser(user);
    PaymentCard cardFromDb = cardRepository.save(card);
    return cardMapper.toDto(cardFromDb);
  }

  @Override
  public PaymentCardDto getById(Long id) {
    PaymentCard card = cardRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(CARD_NOT_FOUND));
    if (!card.isActive()) {
      throw new UserServiceException(CARD_NOT_FOUND);
    }
    return cardMapper.toDto(card);
  }

  @Override
  public Page<PaymentCardDto> getAll(String name, String surname, Pageable pageable) {
    Specification<PaymentCard> spec = PaymentCardSpecification.hasUserName(name)
                    .and(PaymentCardSpecification.hasUserSurname(surname));
    return cardRepository.findAll(spec, pageable)
            .map(cardMapper::toDto);
  }

  @Override
  public List<PaymentCardDto> getByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserServiceException(USER_NOT_FOUND);
    }
    return cardRepository.findByUserId(userId)
            .stream()
            .map(cardMapper::toDto)
            .toList();
  }

  @CacheEvict(value = "user", key = "#id")
  @Override
  @Transactional
  public PaymentCardDto update(Long id, PaymentCardDto dto) {
    PaymentCard card = cardRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(CARD_NOT_FOUND));
    card.setNumber(dto.getNumber());
    card.setActive(dto.getActive());
    return cardMapper.toDto(card);
  }

  @CacheEvict(value = "user", key = "#id")
  @Override
  @Transactional
  public void activate(Long id) {
    PaymentCard card = cardRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(CARD_NOT_FOUND));
    card.setActive(true);
  }

  @CacheEvict(value = "user", key = "#id")
  @Override
  @Transactional
  public void delete(Long id) {
    PaymentCard card = cardRepository.findById(id)
            .orElseThrow(() -> new UserServiceException(CARD_NOT_FOUND));
    card.setActive(false);
  }
}
