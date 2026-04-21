package com.innowise.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
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
import org.springframework.test.annotation.DirtiesContext;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserIntegrationTest {

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
  static void registerProps(DynamicPropertyRegistry registry) {
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

  @AfterEach
  void clean() {
    userRepository.deleteAll();
  }

  @Test
  void createUser_success() throws Exception {
    UserDto dto = new UserDto();
    dto.setId(11L);
    dto.setName("Alex");
    dto.setSurname("Artsikhovich");
    dto.setEmail("orientirik111@gmail.com");
    dto.setActive(true);
    mockMvc.perform(post("/api/users")
                    .header("Authorization", userToken(11L))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(11)))
            .andExpect(jsonPath("$.name", is("Alex")));
  }

  @Test
  void getUser_success() throws Exception {
    User user = createTestUser(200L);
    mockMvc.perform(get("/api/users/" + user.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is(user.getEmail())));
  }

  @Test
  void updateUser_success() throws Exception {
    User user = createTestUser(300L);
    UserDto updateDto = new UserDto();
    updateDto.setId(user.getId());
    updateDto.setName("Updated");
    updateDto.setSurname("User");
    updateDto.setEmail("updated300@gmail.com");
    updateDto.setBirthDate(LocalDate.of(1990, 1, 1));
    updateDto.setActive(false);
    mockMvc.perform(put("/api/users/" + user.getId())
                    .header("Authorization", userToken(user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated")))
            .andExpect(jsonPath("$.surname", is("User")))
            .andExpect(jsonPath("$.email", is("updated300@gmail.com")))
            .andExpect(jsonPath("$.birthDate", is("1990-01-01")))
            .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  void activateUser_success() throws Exception {
    User user = createTestUser(500L);
    user.setActive(false);
    userRepository.save(user);
    mockMvc.perform(patch("/api/users/" + user.getId() + "/activate")
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNoContent());
    User updated = userRepository.findById(user.getId()).orElseThrow();
    assertTrue(updated.isActive());
    mockMvc.perform(get("/api/users/" + user.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isOk());
  }

  @Test
  void deactivateUser_success() throws Exception {
    User user = createTestUser(600L);
    mockMvc.perform(patch("/api/users/" + user.getId() + "/deactivate")
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNoContent());
    User updated = userRepository.findById(user.getId()).orElseThrow();
    assertFalse(updated.isActive());
    mockMvc.perform(get("/api/users/" + user.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_hardDelete_success() throws Exception {
    User user = createTestUser(400L);
    mockMvc.perform(delete("/api/users/" + user.getId())
                    .header("X-Saga-Delete", "true"))
            .andExpect(status().isNoContent());
    assertFalse(userRepository.findById(user.getId()).isPresent());
    mockMvc.perform(get("/api/users/" + user.getId())
                    .header("Authorization", userToken(user.getId())))
            .andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_withoutSagaHeader_forbidden() throws Exception {
    User user = createTestUser(401L);
    mockMvc.perform(delete("/api/users/" + user.getId()))
            .andExpect(status().isForbidden());
  }
}