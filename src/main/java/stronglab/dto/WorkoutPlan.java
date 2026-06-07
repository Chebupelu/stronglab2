package stronglab.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import stronglab.model.Athlete;

import java.time.LocalDate;

@Entity
@Table(name = "workoutplan")
@Data
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "athlete_id", referencedColumnName = "id")
    private Athlete athlete;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "workout_date")
    private LocalDate workoutDate;

    private Integer sets;
    private Integer reps;
    private Double weight;

    @Column(name = "calculated_load")
    private Double calculatedLoad;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    private String status;
}
