package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @EntityGraph(value = "user-with-cards", type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT u FROM User u WHERE u.active = true")
  List<User> findActiveUsers();

  @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
  Optional<User> findByEmail(@Param("email") String email);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from User u left join fetch u.cards where u.id = :id")
  Optional<User> findByIdForUpdate(Long id);

  @Override
  @EntityGraph(value = "user-with-cards", type = EntityGraph.EntityGraphType.LOAD)
  Page<User> findAll(Specification<User> spec, Pageable pageable);
}
