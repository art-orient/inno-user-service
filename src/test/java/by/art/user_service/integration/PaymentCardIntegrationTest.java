package by.art.user_service.integration;

import by.art.user_service.TestContainersConfig;
import by.art.user_service.dto.PaymentCardDto;
import by.art.user_service.entity.PaymentCard;
import by.art.user_service.entity.User;
import by.art.user_service.repository.PaymentCardRepository;
import by.art.user_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentCardIntegrationTest extends TestContainersConfig {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PaymentCardRepository cardRepository;
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

  private PaymentCard createTestCard(User user) {
    PaymentCard card = new PaymentCard();
    card.setCardNumber("1234-5678-9999-0000");
    card.setActive(true);
    card.setUser(user);
    return cardRepository.save(card);
  }

  @Test
  void createCard_success() throws Exception {
    User user = createTestUser();
    PaymentCardDto dto = new PaymentCardDto();
    dto.setCardNumber("1234-5678-9999-0000");
    dto.setActive(true);
    dto.setUserId(user.getId());

    mockMvc.perform(post("/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.cardNumber", is("1234-5678-9999-0000")));
  }

  @Test
  void getCardsByUser_success() throws Exception {
    User user = createTestUser();
    createTestCard(user);

    mockMvc.perform(get("/cards/user/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].cardNumber", is("1234-5678-9999-0000")));
  }

  @Test
  void updateCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);
    PaymentCardDto updateDto = new PaymentCardDto();
    updateDto.setCardNumber("9999-8888-7777-6666");
    updateDto.setActive(false);
    updateDto.setUserId(user.getId());

    mockMvc.perform(put("/cards/" + card.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cardNumber", is("9999-8888-7777-6666")))
            .andExpect(jsonPath("$.active", is(false)));
  }

  @Test
  void activateCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);
    card.setActive(false);
    cardRepository.save(card);

    mockMvc.perform(patch("/cards/" + card.getId() + "/activate"))
            .andExpect(status().isOk());
    PaymentCard updated = cardRepository.findById(card.getId()).orElseThrow();
    assert updated.isActive();
  }

  @Test
  void deactivateCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);

    mockMvc.perform(patch("/cards/" + card.getId() + "/deactivate"))
            .andExpect(status().isOk());
    PaymentCard updated = cardRepository.findById(card.getId()).orElseThrow();
    assert !updated.isActive();
  }

  @Test
  void deleteCard_success() throws Exception {
    User user = createTestUser();
    PaymentCard card = createTestCard(user);
    mockMvc.perform(delete("/cards/" + card.getId()))
            .andExpect(status().isNoContent());
    assert cardRepository.findById(card.getId()).isEmpty();
  }
}
