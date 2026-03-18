package by.art.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NamedEntityGraph(
        name = "card-with-user",
        attributeNodes = {
                @NamedAttributeNode("user")
        }
)
@Table(name = "payment_cards")
@NamedQuery(name = "PaymentCard.findActive", query = "SELECT c FROM PaymentCard c WHERE c.active = true")
@Getter @Setter
public class PaymentCard extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private String number;
  private String holder;
  private LocalDate expirationDate;
  private boolean active = true;
}
