package by.art.user_service.repository.specification;

import by.art.user_service.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {

  public static Specification<User> hasName(String name) {
    return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (name == null || name.isBlank()) {
        return null;
      }
      return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<User> hasSurname(String surname) {
    return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (surname == null || surname.isBlank()) {
        return null;
      }
      return cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    };
  }
}