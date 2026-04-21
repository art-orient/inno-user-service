package com.innowise.userservice.integration;

import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminPaymentCardIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> postgres =
          new PostgreSQLContainer<>("postgres:15")
                  .withDatabaseName("testdb")
                  .withUsername("test")
                  .withPassword("test");

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PaymentCardRepository cardRepository;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @BeforeAll
  static void checkDocker() {
    Assumptions.assumeTrue(
            DockerClientFactory.instance().isDockerAvailable(),
            "Skipping integration tests because Docker is not available"
    );
  }

  private String adminToken(Long adminId) {
    byte[] key = Decoders.BASE64.decode(jwtSecret);
    String jwt = Jwts.builder()
            .claim("userId", adminId)
            .claim("role", "ADMIN")
            .claim("type", "access")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
            .signWith(Keys.hmacShaKeyFor(key))
            .compact();
    return "Bearer " + jwt;
  }

  private User createUser(long id, String name, String surname) {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setSurname(surname);
    user.setEmail("user" + id + "@mail.com");
    user.setActive(true);
    return userRepository.save(user);
  }

  private void createCard(User user, String number) {
    PaymentCard card = new PaymentCard();
    card.setNumber(number);
    card.setActive(true);
    card.setUser(user);
    card.setHolder(user.getName() + " " + user.getSurname());
    card.setExpirationDate(LocalDate.of(2030, 12, 31));
    cardRepository.save(card);
  }

  @AfterEach
  void clean() {
    cardRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void admin_canGetAllCards() throws Exception {
    User u1 = createUser(1L, "Alex", "Ivanov");
    User u2 = createUser(2L, "Petr", "Sidorov");
    createCard(u1, "1111222233334444");
    createCard(u2, "5555666677778888");
    mockMvc.perform(get("/api/cards")
                    .header("Authorization", adminToken(999L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  void admin_canFilterByName() throws Exception {
    User u1 = createUser(10L, "Alex", "Ivanov");
    User u2 = createUser(11L, "Petr", "Sidorov");
    createCard(u1, "1111222233334444");
    createCard(u2, "5555666677778888");
    mockMvc.perform(get("/api/cards")
                    .param("name", "Alex")
                    .header("Authorization", adminToken(999L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].holder", containsString("Alex")));
  }

  @Test
  void admin_canFilterBySurname() throws Exception {
    User u1 = createUser(20L, "Alex", "Ivanov");
    User u2 = createUser(21L, "Petr", "Sidorov");
    createCard(u1, "1111222233334444");
    createCard(u2, "5555666677778888");
    mockMvc.perform(get("/api/cards")
                    .param("surname", "Sidorov")
                    .header("Authorization", adminToken(999L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].holder", containsString("Sidorov")));
  }
}