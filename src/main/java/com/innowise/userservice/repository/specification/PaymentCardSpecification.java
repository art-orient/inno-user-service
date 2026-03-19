package com.innowise.userservice.repository.specification;

import com.innowise.userservice.entity.PaymentCard;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class PaymentCardSpecification {
  private PaymentCardSpecification() {
  }

  public static Specification<PaymentCard> hasUserName(String name) {
    return (Root<PaymentCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (name == null || name.isBlank()) {
        return null;
      }

      Join<Object, Object> userJoin = root.join("user", JoinType.INNER);

      return cb.like(
              cb.lower(userJoin.get("name")),
              "%" + name.toLowerCase() + "%"
      );
    };
  }

  public static Specification<PaymentCard> hasUserSurname(String surname) {
    return (Root<PaymentCard> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (surname == null || surname.isBlank()) {
        return null;
      }

      Join<Object, Object> userJoin = root.join("user", JoinType.INNER);

      return cb.like(
              cb.lower(userJoin.get("surname")),
              "%" + surname.toLowerCase() + "%"
      );
    };
  }

  public static Specification<PaymentCard> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("active"));
  }
}
