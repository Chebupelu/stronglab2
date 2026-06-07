package stronglab.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "bodyparams")
@Getter @Setter
public class Bodyparams {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "athlete_id", nullable = false)
  private Athlete athlete;

  private Float weight;
  private Float height;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  public Bodyparams() {}

  public Bodyparams(Athlete athlete, Float weight, Float height, LocalDate date) {
    this.athlete = athlete;
    this.weight = weight;
    this.height = height;
    this.date = date;
  }

  public void setAthleteId(Long athleteId) {
    if (athleteId == null) {
      this.athlete = null;
      return;
    }
    // Если объект еще не создан, инициализируем его
    if (this.athlete == null) {
      this.athlete = new Athlete();
    }
    // Записываем ID внутрь объекта Athlete (убедитесь, что в классе Athlete есть метод setId)
    this.athlete.setId(athleteId);
  }
}