package by.art.user_service.repository;

import by.art.user_service.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>,
        JpaSpecificationExecutor<PaymentCard> {

  @EntityGraph(value = "card-with-user", type = EntityGraph.EntityGraphType.LOAD)
  List<PaymentCard> findAllByActiveTrue();

  @EntityGraph(value = "card-with-user", type = EntityGraph.EntityGraphType.LOAD)
  List<PaymentCard> findAllByUserIdAndActiveTrue(Long userId);

  @Query(value = "SELECT * FROM payment_cards WHERE user_id = :userId", nativeQuery = true)
  List<PaymentCard> findCards(@Param("userId") Long userId);

  @Override
  @EntityGraph(value = "card-with-user", type = EntityGraph.EntityGraphType.LOAD)
  Page<PaymentCard> findAll(Specification<PaymentCard> spec, Pageable pageable);
}
