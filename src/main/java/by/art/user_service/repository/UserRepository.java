package by.art.user_service.repository;

import by.art.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  @Query("SELECT u FROM User u WHERE u.active = true")
  List<User> findActiveUsers();

  @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
  User findByEmail(@Param("email") String email);
}
