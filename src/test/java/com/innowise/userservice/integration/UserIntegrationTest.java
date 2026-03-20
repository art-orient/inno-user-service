package com.innowise.userservice.integration;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class UserIntegrationTest {

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
  }

  @Test
  void createUser_success() throws Exception {
    UserDto dto = new UserDto();
    dto.setName("Alex");
    dto.setSurname("Artsikhovich");
    dto.setEmail("orientirik@gmail.com");
    dto.setActive(true);

    mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", is("Alex")))
            .andExpect(jsonPath("$.surname", is("Artsikhovich")));
  }

  @Test
  void getUser_success() throws Exception {
    User user = createTestUser();
    user = userRepository.save(user);

    mockMvc.perform(get("/users/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.email", is("orientirik@gmail.com")));
  }

  @Test
  void updateUser_success() throws Exception {
    User user = createTestUser();
    user = userRepository.save(user);

    UserDto updateDto = new UserDto();
    updateDto.setName("Updated");
    updateDto.setSurname("User");
    updateDto.setEmail("updated@gmail.com");
    updateDto.setActive(false);
    mockMvc.perform(put("/users/" + user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated")))
            .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  void deleteUser_softDelete_success() throws Exception {
    User user = createTestUser();
    user = userRepository.save(user);
    mockMvc.perform(delete("/users/" + user.getId()))
            .andExpect(status().isNoContent());
    User deletedUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new AssertionError("User must still exist in DB"));
    assertFalse(deletedUser.isActive(), "User must be soft-deleted (active=false)");
    mockMvc.perform(get("/users/" + user.getId()))
            .andExpect(status().isNotFound());
  }
}
