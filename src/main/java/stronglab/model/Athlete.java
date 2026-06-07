package stronglab.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "athlete")
@Getter @Setter
public class Athlete {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // Связь 1 к 1 с таблицей User
  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  // Связь Многие (Атлеты) к Одному (Тренеру)
  @ManyToOne
  @JoinColumn(name = "trainer_id")
  private Trainer trainer;

  @Column(name = "fitness_level")
  private String fitnessLevel;


}