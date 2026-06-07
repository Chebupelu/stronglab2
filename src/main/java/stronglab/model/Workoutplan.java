package stronglab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "workoutplan")
@Getter @Setter
public class Workoutplan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "athlete_id", nullable = false)
  @JsonIgnoreProperties({"workouts", "trainer", "password"})
  private Athlete athlete;

  @Column(name = "calculated_load")
  private Double calculatedLoad;

  private long reps;

  private long sets;

  private Double weight;

  @Column(name = "exercise_name")
  private String exerciseName;

  @Column(name = "workout_date")
  private LocalDate workoutDate;

  @Column(name = "is_completed")
  private Boolean isCompleted;

  private String status;

}