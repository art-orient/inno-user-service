package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.PaymentCardDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class PaymentCardIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> postgres =
          new PostgreSQLContainer<>("postgres:15")
                  .withDatabaseName("testdb")
                  .withUsername("test")
                  .withPassword("test");

  @Container
  static final GenericContainer<?> redis =
          new GenericContainer<>("redis:7.2")
                  .withExposedPorts(6379);

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PaymentCardRepository cardRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @BeforeAll
  static void checkDocker() {
    Assumptions.assumeTrue(
            DockerClientFactory.instance().isDockerAvailable(),
            "Skipping integration tests because Docker is not available"
    );
  }

  private String token(Long userId, String role) {
    byte[] key = Decoders.BASE64.decode(jwtSecret);
    String jwt = Jwts.builder()
            .claim("userId", userId)
            .claim("role", role)
            .claim("type", "access")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
            .signWith(Keys.hmacShaKeyFor(key))
            .compact();
    return "Bearer " + jwt;
  }

  private String userToken(Long userId) {
    return token(userId, "USER");
  }

  private User createTestUser(long id) {
    User user = new User();
    user.setId(id);
    user.setName("Alex");
    user.setSurname("Artsikhovich");
    user.setEmail("orientirik" + id + "@gmail.com");
    user.setActive(true);
    return userRepository.save(user);
  }

  private PaymentCard createTestCard(User user) {
    PaymentCard card = new PaymentCard();
    card.setNumber("1234567899990000");
    card.setActive(true);
    card.setUser(user);
    card.setHolder("Test Holder");
    card.setExpirationDate(LocalDate.of(2030, 12, 31));
    return cardRepository.save(card);
  }

  @AfterEach
  void clean() {
    cardRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void createCard_success() throws Exception {
    User user = createTestUser(100L);
    PaymentCardDto dto = new PaymentCardDto();
    dto.setNumber("1234567899990000");
    dto.setActive(true);
    dto.setUserId(user.getId());
    dto.setHolder("Alex Artsikhovich");
    dto.setExpirationDate(LocalDate.of(2030, 12, 31));
    mockMvc.perform(post("/api/cards")
                    .header("Authorization", userToken(user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.number", is("1234567899990000")));
  }

  @Test
  void getCardsByUser_success() throws Exception {
    User user = createTestUser(200L);
    createTestCard(user);
    mockMvc.perform(get("/api/cards/users/" + user.getId() + "/payment-cards")
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].number", is("1234567899990000")));
  }

  @Test
  void updateCard_success() throws Exception {
    User user = createTestUser(300L);
    PaymentCard card = createTestCard(user);
    PaymentCardDto updateDto = new PaymentCardDto();
    updateDto.setNumber("9999888877776666");
    updateDto.setActive(false);
    updateDto.setUserId(user.getId());
    updateDto.setHolder("Alex Artsikhovich");
    updateDto.setExpirationDate(LocalDate.of(2030, 12, 31));
    mockMvc.perform(put("/api/cards/" + card.getId())
                    .header("Authorization", userToken(user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number", is("9999888877776666")))
            .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  void activateCard_success() throws Exception {
    User user = createTestUser(400L);
    PaymentCard card = createTestCard(user);
    card.setActive(false);
    cardRepository.save(card);
    mockMvc.perform(patch("/api/cards/" + card.getId() + "/status")
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNoContent());
    PaymentCard updated = cardRepository.findById(card.getId()).orElseThrow();
    assert updated.isActive();
  }

  @Test
  void deleteCard_softDelete_success() throws Exception {
    User user = createTestUser(500L);
    PaymentCard card = createTestCard(user);
    mockMvc.perform(delete("/api/cards/" + card.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNoContent());
    PaymentCard deletedCard = cardRepository.findById(card.getId())
            .orElseThrow();
    assertFalse(deletedCard.isActive());
    mockMvc.perform(get("/api/cards/" + card.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNotFound());
  }
}