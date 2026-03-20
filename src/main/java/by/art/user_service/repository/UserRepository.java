package by.art.user_service.repository;

import by.art.user_service.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  @Query("SELECT u FROM User u WHERE u.active = true")
  List<User> findActiveUsers();

  @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
  User findByEmail(@Param("email") String email);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select u from User u left join fetch u.cards where u.id = :id")
  Optional<User> findByIdForUpdate(Long id);
}
