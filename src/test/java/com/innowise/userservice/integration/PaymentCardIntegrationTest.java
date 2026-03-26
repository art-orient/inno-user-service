package com.innowise.userservice.integration;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class PaymentCardIntegrationTest {

  @Container
  static final PostgreSQLContainer<?> postgres =
          new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
                  .withDatabaseName("testdb")
                  .withUsername("test")
                  .withPassword("test");

  @Container
  static final GenericContainer<?> redis =
          new GenericContainer<>(DockerImageName.parse("redis:7.2"))
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

  @BeforeAll
  static void checkDocker() {
    Assumptions.assumeTrue(
            DockerClientFactory.instance().isDockerAvailable(),
            "Skipping integration tests because Docker is not available"
    );
  }

  private User createTestUser() {
    User user = new User();
    user.setName("Alex");
    user.setSurname("Artsikhovich");
    user.setEmail("orientirik@gmail.com");
    user.setActive(true);
    return userRepository.save(user);
  }

  @AfterEach
  void clean() {
    userRepository.deleteAll();
    cardRepository.deleteAll();
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

  @Test
  void debugEnv() {
    System.out.println("JWT_SECRET=" + System.getenv("JWT_SECRET"));
  }

  @Test
  void createCard_success() throws Exception {
    User user = createTestUser();
    PaymentCardDto dto = new PaymentCardDto();
    dto.setNumber("1234567899990000");
    dto.setActive(true);
    dto.setUserId(user.getId());
    dto.setHolder("Alex Artsikhovich");
    dto.setExpirationDate(LocalDate.of(2030, 12, 31));

    mockMvc.perform(post("/payment-cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.number", is("1234567899990000")));
  }

  @Test
  void getCardsByUser_success() throws Exception {
    User user = createTestUser();
    createTestCard(user);

    mockMvc.perform(get("/payment-cards/users/" + user.getId() + "/payment-cards"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].number", is("1234567899990000")));
  }

  @Test
  void updateCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);
    PaymentCardDto updateDto = new PaymentCardDto();
    updateDto.setNumber("9999888877776666");
    updateDto.setActive(false);
    updateDto.setUserId(user.getId());
    updateDto.setHolder("Alex Artsikhovich");
    updateDto.setExpirationDate(LocalDate.of(2030, 12, 31));

    mockMvc.perform(put("/payment-cards/" + card.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number", is("9999888877776666")))
            .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  void activateCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);
    card.setActive(false);
    cardRepository.save(card);

    mockMvc.perform(patch("/payment-cards/" + card.getId() + "/status"))
            .andExpect(status().isNoContent());
    PaymentCard updated = cardRepository.findById(card.getId()).orElseThrow();
    assert updated.isActive();
  }

  @Test
  void deleteCard_softDelete_success() throws Exception {
    User user = createTestUser();
    user = userRepository.save(user);
    PaymentCard card = createTestCard(user);
    card = cardRepository.save(card);
    mockMvc.perform(delete("/payment-cards/" + card.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());
    PaymentCard deletedCard = cardRepository.findById(card.getId())
            .orElseThrow(() -> new AssertionError("Card must still exist in DB"));
    assertFalse(deletedCard.isActive(), "Card must be soft-deleted (active=false)");
    mockMvc.perform(get("/payment-cards/" + card.getId()))
            .andExpect(status().isNotFound());
  }
}
