package stronglab.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Entity
@Table(name = "goal")
@Getter
@Setter
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "athlete_id", nullable = false)
  private Athlete athlete;

  @Column(name = "exercise_type")
  private String exerciseType;

  @Column(name = "target_weight")
  private double targetWeight;

  @Column(name = "target_reps")
  private int targetReps;

  @Column(name = "current_max_weight")
  private double currentMaxWeight;

  @Column(name = "current_max_reps")
  private int currentMaxReps;

  @Column(name = "is_status")
  private String isStatus;

  @Column(name = "deadline")
  private Date deadline;

  @Column(name = "created_at")
  private Date createdAt;
}