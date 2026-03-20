package by.art.user_service.integration;

import by.art.user_service.TestContainersConfig;
import by.art.user_service.dto.UserDto;
import by.art.user_service.entity.User;
import by.art.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserIntegrationTest extends TestContainersConfig {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ObjectMapper objectMapper;

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
  void deleteUser_success() throws Exception {
    User user = createTestUser();
    user = userRepository.save(user);

    mockMvc.perform(delete("/users/" + user.getId()))
            .andExpect(status().isNoContent());
    assertFalse(userRepository.findById(user.getId()).isPresent());
  }
}
